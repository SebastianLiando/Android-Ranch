package com.zetzaus.dragandraw;

import androidx.fragment.app.Fragment;

/**
 * This activity holds the fragment <code>DragAndDrawFragment</code>.
 */
public class DragAndDrawActivity extends SingleFragmentActivity {

    /**
     * Returns the fragment <code>DragAndDrawFragment</code>.
     *
     * @return <code>DragAndDrawFragment</code>.
     */
    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}
