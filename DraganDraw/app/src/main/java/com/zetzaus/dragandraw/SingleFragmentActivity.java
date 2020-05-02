package com.zetzaus.dragandraw;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Parent class for all activity that displays a single fragment for the whole screen.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Displays fragment on the screen.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(android.R.id.content) == null) {
            manager.beginTransaction()
                    .add(android.R.id.content, createFragment())
                    .commit();
        }
    }

    protected abstract Fragment createFragment();
}
