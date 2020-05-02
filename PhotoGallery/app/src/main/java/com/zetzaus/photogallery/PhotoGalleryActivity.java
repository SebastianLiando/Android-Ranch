package com.zetzaus.photogallery;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

/**
 * This activity displays <code>PhotoGalleryFragment</code>.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity {

    /**
     * Returns an intent directed to this activity.
     *
     * @param context the caller context.
     * @return an intent directed to this activity.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    /**
     * Returns a <code>PhotoGalleryFragment</code>.
     *
     * @return a <code>PhotoGalleryFragment</code>.
     */
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
