package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;

/**
 * This activity displays the <code>CrimeFragment</code>.
 */
public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_UUID_KEY = BuildConfig.APPLICATION_ID + "uuid key";

    /**
     * Returns a new <code>CrimeFragment</code>.
     *
     * @return the fragment to display.
     */
    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_UUID_KEY);
        return CrimeFragment.newInstance(crimeId);
    }

    /**
     * Returns a new explicit intent directed to this activity. Requires crime id to be passed
     * to the fragment.
     *
     * @param context the starting context.
     * @param crimeId the crime id.
     * @return an explicit intent directed to this activity.
     */
    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_UUID_KEY, crimeId);
        return intent;
    }
}
