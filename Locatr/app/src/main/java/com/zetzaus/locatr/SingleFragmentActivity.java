package com.zetzaus.locatr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * This class is the parent class for <code>Activity</code> that displays a single fragment for the whole screen.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Displays the fragment on the screen.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment content = getSupportFragmentManager().findFragmentById(android.R.id.content);

        if (content == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, createFragment())
                    .commit();
        }
    }

    protected abstract Fragment createFragment();
}
