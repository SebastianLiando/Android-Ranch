package com.zetzaus.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final float ITEM_COLUMN_WIDTH = 400f;

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private ThumbnailDownloader<PhotoAdapter.ViewHolder> mDownloader;

    private List<GalleryItem> mItems = new ArrayList<>();
    private int mPage = 1;

    /**
     * Returns an instance of this fragment.
     *
     * @return an instance of this fragment.
     */
    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        return fragment;
    }

    /**
     * Retains this fragment on device configuration change and start the downloader thread.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask(mPage).execute();

        // Start downloader thread
        Handler handler = new Handler();
        mDownloader = new ThumbnailDownloader<>(handler);
        mDownloader.setDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoAdapter.ViewHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoAdapter.ViewHolder target, Bitmap thumbnail) {
                if (target != null) {
                    Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                    target.bindDrawable(drawable);
                }
            }
        });
        mDownloader.start();
        // Block main thread until ready
        mDownloader.getLooper();
        Log.i(TAG, "Thumbnail downloader started");
    }

    /**
     * Clear the Handler's message queue when the <code>View</code> is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Handle rotation
        mDownloader.clearQueue();
    }

    /**
     * Stops the downloader thread.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloader.quit();
        Log.i(TAG, "Thumbnail downloader stopped");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        // Setup RecyclerView
        mRecyclerView = v.findViewById(R.id.recycler_view_photos);
        setupAdapter();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!mRecyclerView.canScrollVertically(1)) {
                    // Get the next page
                    new FetchItemTask(++mPage).execute();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GridLayoutManager manager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int start = max(manager.findFirstVisibleItemPosition() - 10, 0);
                    int end = min(manager.findLastVisibleItemPosition() + 10, mItems.size() - 1);
                    // Preload previous 10
                    for (int i = start; i < manager.findFirstVisibleItemPosition(); i++) {
                        String url = mItems.get(i).getURL();
                        if (mDownloader.retrieveCache(url) == null)
                            mDownloader.queuePreload(url);

                    }
                    // Preload next 10
                    for (int i = manager.findLastVisibleItemPosition() + 1; i <= end; i++) {
                        String url = mItems.get(i).getURL();
                        if (mDownloader.retrieveCache(url) == null)
                            mDownloader.queuePreload(url);

                    }
                }
            }
        });
        mRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int cols = Math.round(mRecyclerView.getWidth() / ITEM_COLUMN_WIDTH);
                        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), cols));
                        // Done and remove
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        return v;
    }

    /**
     * Attaches <code>PhotoAdapter</code> to the <code>RecyclerView</code>.
     */
    private void setupAdapter() {
        // AsyncTask callbacks may call when the fragment is not attached
        if (isAdded()) {
            if (mRecyclerView.getAdapter() == null) {
                mPhotoAdapter = new PhotoAdapter(mItems);
                mRecyclerView.setAdapter(mPhotoAdapter);
            }
        }
    }

    private class FetchItemTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private int mPage;

        /**
         * Creates a <code>FetchItemTask</code> for a page number.
         *
         * @param page the page number.
         */
        public FetchItemTask(int page) {
            mPage = page;
        }

        /**
         * Fetches photo data from Flickr.
         *
         * @param voids nothing.
         * @return list of photo data.
         */
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItems(mPage);
        }

        /**
         * Adds the photo data to the list and notify adapter of such change.
         *
         * @param galleryItems the new photo data to be added.
         */
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems.addAll(galleryItems);
            mPhotoAdapter.notifyItemRangeInserted(mItems.size() - 100, 100);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_photo, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_placeholder);
            holder.bindDrawable(drawable);
            String url = mGalleryItems.get(position).getURL();
            Bitmap cacheBitmap = mDownloader.retrieveCache(holder, url);
            if (cacheBitmap != null) {
                holder.bindDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            } else {
                mDownloader.queueThumbnail(holder, url);
            }
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView mImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView;
            }

            public void bindDrawable(Drawable drawable) {
                mImageView.setImageDrawable(drawable);
            }
        }
    }
}
