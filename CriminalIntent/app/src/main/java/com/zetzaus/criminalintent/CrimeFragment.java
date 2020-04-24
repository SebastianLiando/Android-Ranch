package com.zetzaus.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * This fragment displays the details of a crime.
 */
public class CrimeFragment extends Fragment {

    private static final String TAG_DATE_PICKER = DatePickerFragment.class.getSimpleName();
    private static final String TAG_TIME_PICKER = TimePickerFragment.class.getSimpleName();

    private static final String ARG_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;

    private EditText mEditTextTitle;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mCheckBoxSolved;
    private CheckBox mCheckBoxPolice;

    /**
     * Creates a new instance of the <code>CrimeFragment</code>.
     *
     * @param crimeId the crime id to be used.
     * @return this fragment.
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_UUID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Creates the fragment, binds the correct crime.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get crime
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_UUID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        // Tell about menu
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the layout and sets up the functionality.
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
        View parent = inflater.inflate(R.layout.fragment_crime, container, false);

        // Setup edit text
        mEditTextTitle = parent.findViewById(R.id.edit_text_title);
        mEditTextTitle.setText(mCrime.getTitle());
        mEditTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set the date button
        mDateButton = parent.findViewById(R.id.button_crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    FragmentManager manager = getFragmentManager();
                    PickerFragment fragment = DatePickerFragment.newInstance(mCrime.getDate());
                    fragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    fragment.show(manager, TAG_DATE_PICKER);
                } else {
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });

        // Setup time button
        mTimeButton = parent.findViewById(R.id.button_crime_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    FragmentManager manager = getFragmentManager();
                    PickerFragment fragment = TimePickerFragment.newInstance(mCrime.getDate());
                    fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    fragment.show(manager, TAG_TIME_PICKER);
                } else {
                    Intent intent = TimePickerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivityForResult(intent, REQUEST_TIME);
                }
            }
        });

        updateDateTime();

        // Setup solved check box
        mCheckBoxSolved = parent.findViewById(R.id.checkbox_crime_solved);
        mCheckBoxSolved.setChecked(mCrime.isSolved());
        mCheckBoxSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        // Setup requires police check box
        mCheckBoxPolice = parent.findViewById(R.id.checkbox_requires_police);
        mCheckBoxPolice.setChecked(mCrime.isRequiresPolice());
        mCheckBoxPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
            }
        });

        return parent;
    }

    /**
     * Saves crime details to the database when the activity enters pause mode.
     */
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    /**
     * Retrieves the new date from the date picker.
     *
     * @param requestCode the request code.
     * @param resultCode  the result code.
     * @param data        contains the new date.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == Activity.RESULT_OK) {
            // Get new date
            Date newDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(newDate);

            // Get old date
            Date oldDate = mCrime.getDate();
            Calendar oldCalendar = Calendar.getInstance();
            oldCalendar.setTime(oldDate);

            // Set new date or time
            if (requestCode == REQUEST_DATE) {
                oldCalendar.set(Calendar.YEAR, newCalendar.get(Calendar.YEAR));
                oldCalendar.set(Calendar.MONTH, newCalendar.get(Calendar.MONTH));
                oldCalendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH));
            } else if (requestCode == REQUEST_TIME) {
                oldCalendar.set(Calendar.HOUR_OF_DAY, newCalendar.get(Calendar.HOUR_OF_DAY));
                oldCalendar.set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE));
            }

            mCrime.setDate(oldCalendar.getTime());
            updateDateTime();
        }
    }

    /**
     * Inflates menu to the toolbar.
     *
     * @param menu     the menu to be inflated.
     * @param inflater the inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    /**
     * Handles action when a menu item is selected.
     *
     * @param item the selected menu item.
     * @return true if the action is handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_delete:
                CrimeLab.getInstance(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the buttons that displays date and time.
     */
    private void updateDateTime() {
        mDateButton.setText(mCrime.getDateString());
        mTimeButton.setText(mCrime.getTimeString());
    }

}
