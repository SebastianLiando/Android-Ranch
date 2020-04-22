package com.zetzaus.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab instance = null;

    private Context mContext;
    private List<Crime> mCrimeList;

    private CrimeLab(Context context) {
        mContext = context;
        mCrimeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + (i + 1));
            crime.setSolved((i % 2) == 0);
            if (i > 48 && i < 55) crime.setRequiresPolice(true);
            mCrimeList.add(crime);
        }
    }

    public static CrimeLab getInstance(Context context) {
        if (instance == null) {
            instance = new CrimeLab(context);
        }

        return instance;
    }

    public List<Crime> getCrimes() {
        return mCrimeList;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimeList) {
            if (crime.getId().equals(id)) return crime;
        }

        return null;
    }
}
