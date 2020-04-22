package com.zetzaus.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This fragments displays a list of crimes to the screen.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mRecyclerViewCrime;
    private CrimeAdapter mCrimeAdapter;

    /**
     * Inflates the layout of the fragment and sets up the <code>RecyclerView</code>.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the view to display.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // Setup RecyclerView
        mRecyclerViewCrime = parent.findViewById(R.id.recycler_view_crimes);
        mRecyclerViewCrime.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachAdapter();

        return parent;
    }

    /**
     * Attaches a <code>CrimeAdapter</code> to the <code>RecyclerView</code>.
     */
    private void attachAdapter() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        mCrimeAdapter = new CrimeAdapter(crimeLab.getCrimes());
        mRecyclerViewCrime.setAdapter(mCrimeAdapter);
    }
}
