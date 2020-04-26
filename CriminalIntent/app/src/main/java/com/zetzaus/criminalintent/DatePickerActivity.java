package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;

/**
 * This activity holds the fragment <code>DatePickerFragment</code>.
 */
public class DatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Crime mCrime;

    /**
     * Returns a new instance of this fragment.
     *
     * @param context the context.
     * @param crimeId the crime id.
     * @return a new instance of this fragment.
     */
    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_UUID, crimeId);
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
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_UUID);
        mCrime = CrimeLab.getInstance(this).getCrime(crimeId);
        // Launch
        return DatePickerFragment.newInstance(mCrime.getDate());
    }
}
