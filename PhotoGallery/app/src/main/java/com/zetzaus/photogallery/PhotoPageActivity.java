package com.zetzaus.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    PhotoPageFragment mFragment;

    public static Intent newIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(imageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        if (!mFragment.onBackPressed())
            super.onBackPressed();
    }
}
