package com.zetzaus.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class is a singleton class that holds the list of crimes.
 */
public class CrimeLab {

    private static CrimeLab instance = null;

    private Context mContext;
    private LinkedHashMap<UUID, Crime> mCrimeList;

    private CrimeLab(Context context) {
        mContext = context;
        mCrimeList = new LinkedHashMap<>();

        // Dummy fill
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + (i + 1));
            crime.setSolved((i % 2) == 0);
            if (i > 48 && i < 55) crime.setRequiresPolice(true);
            mCrimeList.put(crime.getId(), crime);
        }
    }

    /**
     * Returns the reference to this class.
     *
     * @param context the context.
     * @return the reference to this class.
     */
    public static CrimeLab getInstance(Context context) {
        if (instance == null) {
            instance = new CrimeLab(context);
        }

        return instance;
    }

    /**
     * Returns the list of crimes.
     *
     * @return the list of crimes.
     */
    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimeList.values());
    }

    /**
     * Returns the crime with the specific id.
     *
     * @param id the specific id.
     * @return the corresponding crime.
     */
    public Crime getCrime(UUID id) {
        return mCrimeList.get(id);
    }
}
