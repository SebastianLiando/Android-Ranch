package com.zetzaus.criminalintent;

import android.app.Application;

import com.zetzaus.criminalintent.database.CrimeRepository;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class CrimeDetailsViewModel extends AndroidViewModel {

    private CrimeRepository mRepository;
    private MutableLiveData<UUID> mUUIDMutableLiveData;
    private LiveData<Crime> mCrimeLiveData = Transformations.switchMap(mUUIDMutableLiveData,
            new Function<UUID, LiveData<Crime>>() {
                @Override
                public LiveData<Crime> apply(UUID input) {
                    return mRepository.getCrime(input);
                }
            });

    public CrimeDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepository = CrimeRepository.getInstance(application);
    }

    public void loadCrime(UUID id) {
        mUUIDMutableLiveData.setValue(id);
    }

    public void saveCrime(Crime crime) {
        mRepository.updateCrime(crime);
    }

    public LiveData<Crime> getCrimeLiveData() {
        return mCrimeLiveData;
    }
}
