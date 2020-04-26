package com.zetzaus.criminalintent;

import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This fragment is used to pick a date for a crime.
 */
public class DatePickerFragment extends PickerFragment {

    private DatePicker mDatePicker;

    /**
     * Returns a new instance of this fragment.
     *
     * @param date the crime date.
     * @return a new instance of this fragment.
     */
    public static DatePickerFragment newInstance(Date date) {
        return (DatePickerFragment) PickerFragment.createInstance(new DatePickerFragment(), date);
    }

    /**
     * Returns an initialized picker to be the initial value.
     *
     * @param parent      the parent view.
     * @param initialDate the initial date.
     * @return the initialized picker.
     */
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

    /**
     * Returns the date shown by the <code>DatePicker</code>.
     *
     * @param picker the <code>DatePicker</code>.
     * @return the date shown by the <code>DatePicker</code>.
     */
    @Override
    public Date getPickerValue(FrameLayout picker) {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        return new GregorianCalendar(year, month, day).getTime();
    }

    /**
     * Returns the layout id for <code>DatePicker</code>.
     *
     * @return the layout id for <code>DatePicker</code>.
     */
    @Override
    public int getLayoutResource() {
        return R.layout.dialog_date_picker;
    }
}
