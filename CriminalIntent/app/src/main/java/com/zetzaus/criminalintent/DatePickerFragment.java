package com.zetzaus.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends PickerFragment {

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        return (DatePickerFragment) PickerFragment.createInstance(new DatePickerFragment(), date);
    }

    @Override
    public FrameLayout initPicker(View parent, Date initialDate) {
        mDatePicker = parent.findViewById(R.id.date_picker);
        // Set date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);
        int y = calendar.get(Calendar.YEAR);
        final int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(y, m, d, null);

        return mDatePicker;
    }

    @Override
    public Date getPickerValue(FrameLayout picker) {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        return new GregorianCalendar(year, month, day).getTime();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.dialog_date_picker;
    }
}
