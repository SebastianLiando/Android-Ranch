package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;

public class DatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private Crime mCrime;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_UUID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        // Retrieve crime
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_UUID);
        mCrime = CrimeLab.getInstance(this).getCrime(crimeId);
        // Launch
        return DatePickerFragment.newInstance(mCrime.getDate());
    }
}
