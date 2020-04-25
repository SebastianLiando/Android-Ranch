package com.zetzaus.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This activity displays a view pager that consists of crime details.
 */
public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callback {

    private static final String EXTRA_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";

    private ViewPager2 mCrimePager;
    private ViewPager2.OnPageChangeCallback callback;

    private Button mButtonFirst;
    private Button mButtonLast;

    private List<Crime> mCrimes;

    /**
     * Sets up the activity to display the pager.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        // Get references to views
        mCrimePager = findViewById(R.id.pager_crime);
        mButtonLast = findViewById(R.id.button_last);
        mButtonFirst = findViewById(R.id.button_first);

        mCrimes = CrimeLab.getInstance(this).getCrimes();

        // Setup view pager
        mCrimePager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return mCrimes.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }
        });

        callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mButtonFirst.setEnabled(true);
                mButtonLast.setEnabled(true);

                if (position == 0) {
                    mButtonFirst.setEnabled(false);
                }

                if (position == mCrimes.size() - 1) {
                    mButtonLast.setEnabled(false);
                }
            }
        };
        mCrimePager.registerOnPageChangeCallback(callback);

        // Set starting page
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_UUID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (id.equals(mCrimes.get(i).getId())) {
                mCrimePager.setCurrentItem(i);
                break;
            }
        }

        // Setup jump to first button
        mButtonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCrimePager.setCurrentItem(0);
            }
        });

        // Setup jump to last button
        mButtonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCrimePager.setCurrentItem(mCrimes.size() - 1);
            }
        });
    }

    /**
     * Unregister callback method from the view pager
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCrimePager.unregisterOnPageChangeCallback(callback);
    }

    /**
     * Returns an explicit intent directed to this class.
     *
     * @param context the starting context.
     * @param id      the starting id of the crime.
     * @return an explicit intent directed to this class.
     */
    public static Intent newIntent(Context context, UUID id) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_UUID, id);
        return intent;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        // Do nothing
    }

    /**
     * Close the activity after deleting a crime.
     */
    @Override
    public void onCrimeDeleted() {
        finish();
    }
}
