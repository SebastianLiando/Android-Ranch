package com.zetzaus.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This fragments displays a list of crimes to the screen.
 */
public class CrimeListFragment extends Fragment {

    private static final String SUBTITLE_SHOWN_KEY = "Subtitle_shown_key";
    private static final String CRIME_COUNT_KEY = "Crime_count_key";

    private RecyclerView mRecyclerViewCrime;
    private TextView mTextViewNoCrime;

    private CrimeAdapter mCrimeAdapter;
    private CrimeListViewModel mViewModel;

    private Callback mCallback;

    /**
     * This interface is a callback for the hosting activity.
     */
    public interface Callback {
        void onCrimeSelected(Crime crime);
    }

    /**
     * Attach the hosting activity as a callback. The activity must implement the callback interface.
     *
     * @param context the hosting activity.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(CrimeListViewModel.class);

        if (savedInstanceState != null) {
            mViewModel.setSubtitleShown(savedInstanceState.getBoolean(SUBTITLE_SHOWN_KEY));
            mViewModel.setCrimeCount(savedInstanceState.getInt(CRIME_COUNT_KEY, 0));
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

        mTextViewNoCrime = parent.findViewById(R.id.text_no_crime);

        // Setup RecyclerView
        mRecyclerViewCrime = parent.findViewById(R.id.recycler_view_crimes);
        mRecyclerViewCrime.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mCrimeAdapter == null) {
            Log.i("CrimeListFragment", "Create a new crime adapter");
            mCrimeAdapter = new CrimeAdapter();
            mCrimeAdapter.setCallback((CrimeListActivity) getActivity());
        }
        mRecyclerViewCrime.setAdapter(mCrimeAdapter);

        // Setup Swipe
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mViewModel.deleteCrime(((CrimeAdapter.ViewHolder) viewHolder).getCrime());
            }
        });
        touchHelper.attachToRecyclerView(mRecyclerViewCrime);

        return parent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getLiveDataCrimes().observe(getViewLifecycleOwner(), new Observer<List<Crime>>() {
            @Override
            public void onChanged(List<Crime> crimes) {
                Log.i("CrimeListFragment", crimes.size() + "");

                if (crimes.size() == 0) {
                    mTextViewNoCrime.setVisibility(View.VISIBLE);
                } else {
                    mTextViewNoCrime.setVisibility(View.GONE);
                }

                mCrimeAdapter.submitList(crimes);
                mViewModel.setCrimeCount(crimes.size());
            }
        });
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
        if (mViewModel.isSubtitleShown()) {
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
                mViewModel.addCrime(crime);
                mCallback.onCrimeSelected(crime);
                return true;
            case R.id.action_show_subtitle:
                mViewModel.setSubtitleShown(!mViewModel.isSubtitleShown());
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        outState.putBoolean(SUBTITLE_SHOWN_KEY, mViewModel.isSubtitleShown());
        outState.putInt(CRIME_COUNT_KEY, mViewModel.getCrimeCount());
    }

    /**
     * Frees the callback.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Attaches a <code>CrimeAdapter</code> to the <code>RecyclerView</code>.
     *
     * @deprecated
     */
    public void updateAdapter() {
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter();
            mCrimeAdapter.setCallback(mCallback);
            mRecyclerViewCrime.setAdapter(mCrimeAdapter);
        }

//        mCrimeAdapter.submitList(mCrimeLab.getCrimes());

    }

    /**
     * Updates the subtitle. This may disable or enable the subtitle.
     */
    private void updateSubtitle() {
        int count = mViewModel.getCrimeCount();
        String subtitle = getResources().getQuantityString(R.plurals.format_subtitle, count, count);

        if (!mViewModel.isSubtitleShown()) subtitle = null;

        AppCompatActivity host = (AppCompatActivity) getActivity();
        host.getSupportActionBar().setSubtitle(subtitle);
    }
}
