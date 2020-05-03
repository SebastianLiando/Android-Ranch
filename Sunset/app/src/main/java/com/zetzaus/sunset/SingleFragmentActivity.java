package com.zetzaus.sunset;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Parent class for all activities that display a fragment for the whole screen.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Adds a fragment to the screen.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, createFragment())
                    .commit();
        }
    }

    protected abstract Fragment createFragment();
}
