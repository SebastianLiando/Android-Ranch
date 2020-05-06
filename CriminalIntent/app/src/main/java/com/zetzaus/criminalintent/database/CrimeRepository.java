package com.zetzaus.criminalintent.database;

import android.content.Context;

import com.zetzaus.criminalintent.Crime;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class CrimeRepository {

    private static CrimeRepository instance = null;
    private CrimeDao mCrimeDao;
    private CrimeDatabase mDatabase;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public static CrimeRepository getInstance(Context context) {
        if (instance == null) instance = new CrimeRepository(context);
        return instance;
    }

    private CrimeRepository(Context context) {
        mDatabase = Room.databaseBuilder(context.getApplicationContext(),
                CrimeDatabase.class,
                CrimeDbSchema.CrimeTable.NAME)
                .build();

        mCrimeDao = mDatabase.crimeDao();
    }

    public LiveData<Crime> getCrime(UUID id) {
        return mCrimeDao.getCrime(id);
    }

    public LiveData<List<Crime>> getCrimes() {
        return mCrimeDao.getCrimes();
    }

    public void addCrime(final Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCrimeDao.addCrime(crime);
            }
        });
    }

    public void updateCrime(final Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCrimeDao.updateCrime(crime);
            }
        });
    }

    public void deleteCrime(final Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCrimeDao.deleteCrime(crime);
            }
        });
    }


}
