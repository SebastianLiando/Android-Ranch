package com.zetzaus.beatbox;

import androidx.fragment.app.Fragment;

/**
 * This <code>Activity</code> displays the <code>BeatBoxFragment</code>.
 */
public class BeatBoxActivity extends SingleFragmentActivity {

    /**
     * Returns the <code>BeatBoxFragment</code> to be displayed.
     *
     * @return the <code>BeatBoxFragment</code> to be displayed.
     */
    @Override
    protected Fragment getFragment() {
        return new BeatBoxFragment();
    }
}
