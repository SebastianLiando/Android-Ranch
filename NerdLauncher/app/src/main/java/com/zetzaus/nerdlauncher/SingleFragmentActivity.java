package com.zetzaus.nerdlauncher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * This class is the parent class of all <code>Activity</code> with a single fragment.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Inflates the fragment on screen.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, createFragment())
                .commit();
    }

    public abstract Fragment createFragment();
}
