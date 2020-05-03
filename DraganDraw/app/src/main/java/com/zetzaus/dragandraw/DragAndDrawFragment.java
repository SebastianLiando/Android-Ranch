package com.zetzaus.dragandraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


/**
 * This fragment provides Canvas to be drawn.
 */
public class DragAndDrawFragment extends Fragment {

    /**
     * Returns an instance of this fragment.
     *
     * @return an instance of this fragment.
     */
    public static DragAndDrawFragment newInstance() {
        return new DragAndDrawFragment();
    }

    /**
     * Inflates the XML layout.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drag_and_draw, container, false);
        return v;
    }
}
