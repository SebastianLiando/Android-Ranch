package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.Date;
import java.util.UUID;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * This activity holds <code>TimePickerFragment</code>.
 */
public class TimePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Date mDate;

    /**
     * Returns a new intent pointing to <code>TimePickerActivity</code>.
     *
     * @param context the context.
     * @param date the crime date.
     * @return a new intent pointing to <code>TimePickerActivity</code>.
     */
    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, TimePickerActivity.class);
        intent.putExtra(EXTRA_UUID, date);
        return intent;
    }

    /**
     * Returns the <code>TimePickerFragment</code>.
     *
     * @return the <code>TimePickerFragment</code>.
     */
    @Override
    protected Fragment createFragment() {
        // Retrieve crime
        mDate = (Date) getIntent().getSerializableExtra(EXTRA_UUID);
        // Launch
        return TimePickerFragment.newInstance(mDate);
    }
}
