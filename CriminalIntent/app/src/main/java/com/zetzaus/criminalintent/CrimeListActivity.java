package com.zetzaus.criminalintent;

import androidx.fragment.app.Fragment;

/**
 * This activity displays the fragment <code>CrimeListFragment</code> on the screen.
 *
 * @see CrimeListFragment
 */
public class CrimeListActivity extends SingleFragmentActivity {

    /**
     * Returns a <code>CrimeListFragment</code>.
     *
     * @return the fragment to display.
     */
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
