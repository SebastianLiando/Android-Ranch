package com.zetzaus.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = BuildConfig.APPLICATION_ID + "EXTRA_DATE";
    private static final String ARG_DATE = BuildConfig.APPLICATION_ID + "DATE_KEY";

    private DatePicker mDatePicker;

    /**
     * Returns a new instance of this fragment, carrying the crime's date.
     *
     * @param date the date of crime.
     * @return a new instance of this fragment.
     */
    public static DatePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get date
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_picker, null);
        mDatePicker = view.findViewById(R.id.date_picker);
        mDatePicker.init(y, m, d, null);


        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.label_date_picker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int dayOfMonth = mDatePicker.getDayOfMonth();
                        Date chosenDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
                        sendResult(Activity.RESULT_OK, chosenDate);
                    }
                })
                .setView(view)
                .create();
    }

    /**
     * Sends the chosen date to the <code>CrimeFragment</code>.
     *
     * @param resultCode the result code.
     * @param date       the chosen date.
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
