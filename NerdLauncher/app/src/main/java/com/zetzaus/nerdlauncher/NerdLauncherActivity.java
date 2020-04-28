package com.zetzaus.nerdlauncher;

import androidx.fragment.app.Fragment;

/**
 * This class holds the <code>NerdLauncherFragment</code>.
 *
 * @see NerdLauncherFragment
 */
public class NerdLauncherActivity extends SingleFragmentActivity {

    /**
     * Returns a <code>NerdLauncherFragment</code>.
     *
     * @return a <code>NerdLauncherFragment</code>.
     */
    @Override
    public Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
