package com.zetzaus.criminalintent.database;

import com.zetzaus.criminalintent.Crime;

import java.util.List;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface CrimeDao {

    @Query("SELECT * " +
            "FROM " + CrimeDbSchema.CrimeTable.NAME + " " +
            "WHERE " + CrimeDbSchema.CrimeTable.CrimeColumn.UUID + " = (:id)")
    LiveData<Crime> getCrime(UUID id);

    @Query("SELECT * FROM " + CrimeDbSchema.CrimeTable.NAME)
    LiveData<List<Crime>> getCrimes();
}
