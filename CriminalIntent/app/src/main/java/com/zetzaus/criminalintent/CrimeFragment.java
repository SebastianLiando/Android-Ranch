package com.zetzaus.criminalintent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This fragment displays the details of a crime.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Crime mCrime;

    private EditText mEditTextTitle;
    private Button mDateButton;
    private CheckBox mCheckBoxSolved;

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
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_UUID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
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

        // Set the date button TODO: change button to enable date picking
        mDateButton = parent.findViewById(R.id.button_crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setEnabled(false);

        // Setup check box
        mCheckBoxSolved = parent.findViewById(R.id.checkbox_crime_solved);
        mCheckBoxSolved.setChecked(mCrime.isSolved());
        mCheckBoxSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return parent;
    }
}
