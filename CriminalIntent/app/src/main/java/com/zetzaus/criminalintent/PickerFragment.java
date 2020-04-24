package com.zetzaus.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public abstract class PickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = BuildConfig.APPLICATION_ID + "EXTRA_DATE";
    private static final String ARG_DATE = BuildConfig.APPLICATION_ID + "DATE_KEY";

    private FrameLayout mPicker;
    private Button mButtonOK;
    private Date mDate;

    /**
     * Returns a new instance of this fragment, carrying the crime's date.
     *
     * @param date the date of crime.
     * @return a new instance of this fragment.
     */
    protected static PickerFragment createInstance(PickerFragment fragment, Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);

        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Inflate the layout and setup the fragment.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get date
        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        View parent = inflater.inflate(getLayoutResource(), container, false);

        mPicker = initPicker(parent, mDate);

        mButtonOK = parent.findViewById(R.id.button_ok);
        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date newDate = getPickerValue(mPicker);
                sendResult(Activity.RESULT_OK, newDate);
            }
        });

        return parent;
    }

    /**
     * Sends the chosen date to the target fragment set by <code>setTargetFragment()</code>
     *
     * @param resultCode the result code.
     * @param date       the chosen date.
     */
    private void sendResult(int resultCode, Date date) {
        // Setup data to be sent back
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        if (getTargetFragment() == null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
            dismiss();
        }
    }

    public abstract FrameLayout initPicker(View parent, Date initialDate);

    public abstract Date getPickerValue(FrameLayout picker);

    public abstract int getLayoutResource();

}
