package com.zetzaus.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private CrimeListViewModel mViewModel;

    private boolean mSubtitleShown = false;
    private Callback mCallback;

    /**
     * This interface is a callback for the hosting activity.
     */
    public interface Callback {
        void onCrimeSelected(Crime crime);

        void onSwipeRemove(UUID id);
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

        if (savedInstanceState != null) {
            mSubtitleShown = savedInstanceState.getBoolean(SUBTITLE_SHOWN_KEY);
        }

        mViewModel = new ViewModelProvider(this).get(CrimeListViewModel.class);
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

        // Setup Swipe
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the crime to remove
                CrimeLab lab = CrimeLab.getInstance(getActivity());
                Crime crime = lab.getCrimeAtPos(viewHolder.getBindingAdapterPosition());
                // Delete and process view
                mCallback.onSwipeRemove(crime.getId());
                lab.deleteCrime(crime.getId());
                updateAdapter();
            }
        });
        touchHelper.attachToRecyclerView(mRecyclerViewCrime);

        return parent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TODO: live data
//        mViewModel.getLiveDataCrimes().observe(getViewLifecycleOwner(), new Observer<List<Crime>>() {
//            @Override
//            public void onChanged(List<Crime> crimes) {
//                mCrimeAdapter.setCrimes(crimes);
//            }
//        });
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
                updateAdapter();
                mCallback.onCrimeSelected(crime);
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
     * Frees the callback.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Attaches a <code>CrimeAdapter</code> to the <code>RecyclerView</code>.
     */
    public void updateAdapter() {
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter();
            mCrimeAdapter.setCallback(mCallback);
            mRecyclerViewCrime.setAdapter(mCrimeAdapter);
        }

        mCrimeAdapter.submitList(mCrimeLab.getCrimes());

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
