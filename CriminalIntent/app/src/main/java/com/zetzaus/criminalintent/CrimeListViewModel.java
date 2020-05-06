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

    public CrimeListViewModel(@NonNull Application application) {
        super(application);
        mRepository = CrimeRepository.getInstance(application);
        mLiveDataCrimes = mRepository.getCrimes();
    }

    public LiveData<List<Crime>> getLiveDataCrimes() {
        return mLiveDataCrimes;
    }
}
