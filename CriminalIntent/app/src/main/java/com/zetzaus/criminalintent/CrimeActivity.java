package com.zetzaus.criminalintent;

import androidx.fragment.app.Fragment;

/**
 * This activity displays the <code>CrimeFragment</code>.
 */
public class CrimeActivity extends SingleFragmentActivity {

    /**
     * Returns a new <code>CrimeFragment</code>.
     *
     * @return the fragment to display.
     */
    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
