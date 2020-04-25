package com.zetzaus.criminalintent;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * This class is the parent class of all activities with only a single fragment.
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
        setContentView(getLayoutResId());

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.frame_container);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.frame_container, fragment)
                    .commit();
        }
    }

    /**
     * Creates and sets up the fragment to be displayed.
     *
     * @return the fragment to be displayed.
     */
    protected abstract Fragment createFragment();

    /**
     * Returns the layout ID to be set.
     *
     * @return the layout ID to be set.
     */
    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_single_fragment;
    }
}
