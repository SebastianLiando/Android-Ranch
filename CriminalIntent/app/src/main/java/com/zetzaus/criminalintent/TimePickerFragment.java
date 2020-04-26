package com.zetzaus.criminalintent;

import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * This fragment is used to set the time of the crime.
 */
public class TimePickerFragment extends PickerFragment {

    private TimePicker mTimePicker;

    /**
     * Returns a new instance of this fragment.
     *
     * @param date the starting date.
     * @return a new instance of this fragment.
     */
    public static TimePickerFragment newInstance(Date date) {
        return (TimePickerFragment) createInstance(new TimePickerFragment(), date);
    }

    /**
     * Initialize default picker value.
     *
     * @param parent      the parent layout view.
     * @param initialDate the default date.
     * @return the picker.
     */
    @Override
    public FrameLayout initPicker(View parent, Date initialDate) {
        // Set time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Set time picker
        mTimePicker = parent.findViewById(R.id.time_picker);
        setToTime(hour, minute);

        return mTimePicker;
    }

    /**
     * Returns the value of the picker.
     *
     * @param picker the picker.
     * @return the value of the picker.
     */
    @Override
    public Date getPickerValue(FrameLayout picker) {
        // Get time
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mTimePicker.getHour();
            minute = mTimePicker.getMinute();
        } else {
            hour = mTimePicker.getCurrentHour();
            minute = mTimePicker.getCurrentMinute();
        }
        // Pack time in calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTime();
    }

    /**
     * Returns the layout resource id for the dialog.
     *
     * @return the layout resource id for the dialog.
     */
    @Override
    public int getLayoutResource() {
        return R.layout.dialog_time_picker;
    }

    /**
     * Set the time picker to the given hour and minute.
     *
     * @param hour   the hour (0-23).
     * @param minute the minute.
     */
    private void setToTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }
    }
}
