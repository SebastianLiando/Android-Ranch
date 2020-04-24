package com.zetzaus.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This fragments displays a list of crimes to the screen.
 */
public class CrimeListFragment extends Fragment {

    private static final String SUBTITLE_SHOWN_KEY = "Subtitle_shown_key";

    private RecyclerView mRecyclerViewCrime;
    private TextView mTextViewNoCrime;

    private CrimeAdapter mCrimeAdapter;
    private CrimeLab mCrimeLab;

    private boolean mSubtitleShown = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mSubtitleShown = savedInstanceState.getBoolean(SUBTITLE_SHOWN_KEY);
        }
    }

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

        mCrimeLab = CrimeLab.getInstance(getActivity());
        mTextViewNoCrime = parent.findViewById(R.id.text_no_crime);

        // Setup RecyclerView
        mRecyclerViewCrime = parent.findViewById(R.id.recycler_view_crimes);
        mRecyclerViewCrime.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateAdapter();

        return parent;
    }

    /**
     * Adds action items to the toolbar.
     *
     * @param menu     the menu to be inflated.
     * @param inflater the inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        // Set the subtitle title
        MenuItem item = menu.findItem(R.id.action_show_subtitle);
        if (mSubtitleShown) {
            item.setTitle(R.string.subtitle_hide);
        } else {
            item.setTitle(R.string.subtitle_show);
        }
    }

    /**
     * Handles action when a menu item is selected.
     *
     * @param item the selected menu item.
     * @return true if handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.action_show_subtitle:
                mSubtitleShown = !mSubtitleShown;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update the list when the user goes back from <code>CrimeFragment</code>.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
        updateSubtitle();

        // Setup text if no crime
        if (mCrimeLab.getCrimes().size() == 0) {
            mTextViewNoCrime.setVisibility(View.VISIBLE);
        } else {
            mTextViewNoCrime.setVisibility(View.GONE);
        }
    }

    /**
     * Saves the visibility of the subtitle to the system state.
     *
     * @param outState the system state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_SHOWN_KEY, mSubtitleShown);
    }

    /**
     * Attaches a <code>CrimeAdapter</code> to the <code>RecyclerView</code>.
     */
    private void updateAdapter() {
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(mCrimeLab.getCrimes());
            mRecyclerViewCrime.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.setCrimes(mCrimeLab.getCrimes());
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Updates the subtitle. This may disable or enable the subtitle.
     */
    private void updateSubtitle() {
        int count = CrimeLab.getInstance(getActivity()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.format_subtitle, count, count);

        if (!mSubtitleShown) subtitle = null;

        AppCompatActivity host = (AppCompatActivity) getActivity();
        host.getSupportActionBar().setSubtitle(subtitle);
    }
}
