package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

import androidx.fragment.app.Fragment;

/**
 * This activity holds the fragment <code>DatePickerFragment</code>.
 */
public class DatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Date mDate;

    /**
     * Returns a new instance of this fragment.
     *
     * @param context the context.
     * @param date    the crime date.
     * @return a new instance of this fragment.
     */
    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_UUID, date);
        return intent;
    }

    /**
     * Returns a new <code>DatePickerFragment</code>.
     *
     * @return a new <code>DatePickerFragment</code>.
     */
    @Override
    protected Fragment createFragment() {
        // Retrieve crime
        mDate = (Date) getIntent().getSerializableExtra(EXTRA_UUID);
        // Launch
        return DatePickerFragment.newInstance(mDate);
    }
}
