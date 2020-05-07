package com.zetzaus.criminalintent.database;

import android.content.Context;

import com.zetzaus.criminalintent.Crime;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class CrimeRepository {

    private static CrimeRepository instance = null;
    private CrimeDao mCrimeDao;
    private CrimeDatabase mDatabase;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private File mFile;

    public static CrimeRepository getInstance(Context context) {
        if (instance == null) instance = new CrimeRepository(context);
        return instance;
    }

    private CrimeRepository(Context context) {
        mDatabase = CrimeDatabase.getDatabase(context.getApplicationContext());

        mCrimeDao = mDatabase.crimeDao();

        mFile = context.getApplicationContext().getFilesDir();
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

    public File getPhotoFile(Crime crime) {
        return new File(mFile, crime.getPhotoFileName());
    }


}
