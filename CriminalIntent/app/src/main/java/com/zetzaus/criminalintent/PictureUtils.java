package com.zetzaus.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
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

    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
