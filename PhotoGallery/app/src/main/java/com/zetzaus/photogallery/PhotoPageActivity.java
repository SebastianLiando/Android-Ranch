package com.zetzaus.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

/**
 * This activity holds the <code>PhotoPageFragment</code>.
 *
 * @see PhotoPageFragment
 */
public class PhotoPageActivity extends SingleFragmentActivity {

    PhotoPageFragment mFragment;

    /**
     * Returns an intent to this activity.
     *
     * @param context  the calling context.
     * @param imageUri the image Uri.
     * @return an intent to this activity.
     */
    public static Intent newIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(imageUri);
        return intent;
    }

    /**
     * Creates the fragment to be displayed in the activity.
     *
     * @return the fragment.
     */
    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    /**
     * Navigate through the <code>WebView</code> history before returning to the previous activity.
     */
    @Override
    public void onBackPressed() {
        if (!mFragment.onBackPressed())
            super.onBackPressed();
    }
}
