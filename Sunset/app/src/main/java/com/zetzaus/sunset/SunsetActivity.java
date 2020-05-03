package com.zetzaus.sunset;

import androidx.fragment.app.Fragment;

/**
 * Displays <code>SunsetFragment</code> in the whole screen.
 */
public class SunsetActivity extends SingleFragmentActivity {

    /**
     * Returns <code>SunsetFragment</code>.
     *
     * @return <code>SunsetFragment</code>.
     */
    @Override
    protected Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
