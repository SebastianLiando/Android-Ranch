package com.zetzaus.beatbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * This class is the parent class of all activity with single fragment.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Inflates the fragment to the layout.
     *
     * @param savedInstanceState the saved system state.
     */
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
