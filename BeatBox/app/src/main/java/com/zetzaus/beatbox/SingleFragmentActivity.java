package com.zetzaus.beatbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_container, getFragment())
                .commit();
    }

    protected abstract Fragment getFragment();

}
