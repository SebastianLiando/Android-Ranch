package com.zetzaus.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = ThumbnailDownloader.class.getSimpleName();

    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentHashMap<T, String> mRequestMap = new ConcurrentHashMap<>();   //thread-safe version of HashMap, used to save the URL
    private ThumbnailDownloadListener<T> mDownloadListener;
    private PhotoLruCache mLruCache;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setDownloadListener(ThumbnailDownloadListener<T> downloadListener) {
        mDownloadListener = downloadListener;
    }

    /**
     * Creates a <code>ThumbnailDownloader</code>.
     *
     * @param responseHandler the main thread's <code>Handler</code>.
     */
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    String url = mRequestMap.get(target);
                    Log.i(TAG, "Handle message with URL: " + url);
                    handleRequest(target);
                } else if (msg.what == MESSAGE_PRELOAD) {
                    preload((String) msg.obj);
                }
            }
        };

        // Initialize Cache
        long maxVirtualMemory = Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = (int) (maxVirtualMemory / 8);
        mLruCache = new PhotoLruCache(cacheSize);
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Download from URL: " + url);

        if (url == null) {
            // If the url is not available (Flickr may not contain the URL)
            mRequestMap.remove(target);
        } else {
            if (mLruCache.get(url) == null) {
                // Put message to the message queue
                mRequestMap.put(target, url);
                mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
            } else {
                mDownloadListener.onThumbnailDownloaded(target, mLruCache.get(url));
            }
        }
    }

    /**
     * Sends a message to preload image if the image is not in the cache.
     *
     * @param url the image url to preload.
     */
    public void queuePreload(String url) {
        if (url != null && mLruCache.get(url) == null) {
            mRequestHandler.obtainMessage(MESSAGE_PRELOAD, url)
                    .sendToTarget();
        }
    }

    /**
     * Downloads the image and puts it in the cache.
     *
     * @param url the image url to preload.
     */
    private void preload(String url) {
        if (mLruCache.get(url) == null) {
            downloadImage(url);
        }
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if (url == null) return;

        Bitmap image = mLruCache.get(url);
        if (image == null) {
            // Download and create bitmap
            image = downloadImage(url);
            Log.i(TAG, "Bitmap image created");
        } else {
            Log.i(TAG, "Bitmap taken from cache");
        }

        // Communicate back to UI thread
        final Bitmap finalImage = image;
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                // Handle user quit and RecyclerView recycling the ViewHolder
                if (mRequestMap.get(target) == null || mHasQuit || !mRequestMap.get(target).equals(url))
                    return;

                mRequestMap.remove(target);
                mDownloadListener.onThumbnailDownloaded(target, finalImage);
            }
        });

    }

    /**
     * Returns the image downloaded from the url.
     *
     * @param url the url address of the image.
     * @return the image downloaded from the url.
     */
    private Bitmap downloadImage(String url) {

        try {
            byte[] imageBytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            mLruCache.put(url, image);

            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Remove every messages posted and clear the map.
     */
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestHandler.removeMessages(MESSAGE_PRELOAD);
        mRequestMap.clear();
    }

    /**
     * Quits the thread and clears the cache.
     *
     * @return
     */
    @Override
    public boolean quit() {
        mHasQuit = true;
        mLruCache.evictAll();
        return super.quit();
    }

    /**
     * A custom class for caching the <code>Bitmap</code> images using the LRU policy.
     */
    private class PhotoLruCache extends LruCache<String, Bitmap> {

        /**
         * Creates the cache.
         *
         * @param maxSize the maximum sum of the sizes of the entries in this cache.
         */
        public PhotoLruCache(int maxSize) {
            super(maxSize);
        }

        /**
         * Returns the size of one entry in the cache.
         *
         * @param key   the key of the cache entry.
         * @param value the bitmap of the cache entry.
         * @return the size of one entry in the cache.
         */
        @Override
        protected int sizeOf(String key, Bitmap value) {
            // Measured in KBs
            return value.getByteCount() / 1024;
        }
    }
}
