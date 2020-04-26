package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;

/**
 * This activity holds <code>TimePickerFragment</code>.
 */
public class TimePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Crime mCrime;

    /**
     * Returns a new intent pointing to <code>TimePickerActivity</code>.
     *
     * @param context the context.
     * @param crimeId the crime id.
     * @return a new intent pointing to <code>TimePickerActivity</code>.
     */
    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, TimePickerActivity.class);
        intent.putExtra(EXTRA_UUID, crimeId);
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
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_UUID);
        mCrime = CrimeLab.getInstance(this).getCrime(crimeId);
        // Launch
        return TimePickerFragment.newInstance(mCrime.getDate());
    }
}
