package com.zetzaus.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * This class is a utility class for image loading. It is used only for learning, but no longer implemented
 * as Glide library enables a more efficient loading.
 */
public class PictureUtils {
    /**
     * Returns a scaled down bitmap object to the desired width and height
     *
     * @param path       the path to the image resource.
     * @param destWidth  the desired width.
     * @param destHeight the desired height.
     * @return the <code>Bitmap</code> object.
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float imgHeight = options.outHeight;
        float imgWidth = options.outWidth;

        int sampleSize = 1;
        if (imgHeight > destHeight || imgWidth > destWidth) {
            float sizeHeight = destHeight / imgHeight;
            float sizeWidth = destWidth / imgWidth;
            // Choose the larger
            sampleSize = Math.round(Math.max(sizeHeight, sizeWidth));
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Returns a bitmap object scaled down by estimate corresponding to the activity's display size.
     *
     * @param path     the path to the image resource.
     * @param activity the activity to be displayed at.
     * @return the <code>Bitmap</code> object.
     */
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    /**
     * Returns a <code>Bitmap</code> using a <code>View</code>'s actual height and width.
     *
     * @param path the path to the image resource.
     * @param view the <code>ImageView</code> to be loaded into.
     * @return the <code>Bitmap</code> object.
     */
    public static Bitmap getAccurateBitmap(String path, ImageView view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = view.getMeasuredHeight();
        options.outWidth = view.getMeasuredWidth();
        return BitmapFactory.decodeFile(path, options);
    }
}
