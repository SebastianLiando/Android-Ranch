package com.zetzaus.criminalintent;

import android.app.Application;

import com.zetzaus.criminalintent.database.CrimeRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CrimeListViewModel extends AndroidViewModel {

    private CrimeRepository mRepository;
    private LiveData<List<Crime>> mLiveDataCrimes;
    private int mCrimeCount;
    private boolean mSubtitleShown;

    public CrimeListViewModel(@NonNull Application application) {
        super(application);
        mRepository = CrimeRepository.getInstance(application);
        mLiveDataCrimes = mRepository.getCrimes();
    }

    public LiveData<List<Crime>> getLiveDataCrimes() {
        return mLiveDataCrimes;
    }

    public void deleteCrime(Crime crime) {
        mRepository.deleteCrime(crime);
    }

    public void addCrime(Crime crime){
        mRepository.addCrime(crime);
    }

    public void setCrimeCount(int crimeCount) {
        this.mCrimeCount = crimeCount;
    }

    public int getCrimeCount() {
        return mCrimeCount;
    }

    public void setSubtitleShown(boolean subtitleShown) {
        mSubtitleShown = subtitleShown;
    }

    public boolean isSubtitleShown() {
        return mSubtitleShown;
    }
}
