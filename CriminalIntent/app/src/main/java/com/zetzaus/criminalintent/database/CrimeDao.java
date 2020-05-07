package com.zetzaus.criminalintent.database;

import com.zetzaus.criminalintent.Crime;

import java.util.List;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CrimeDao {

    @Query("SELECT * " +
            "FROM " + CrimeDbSchema.CrimeTable.NAME + " " +
            "WHERE " + CrimeDbSchema.CrimeTable.CrimeColumn.UUID + " = (:id)")
    LiveData<Crime> getCrime(UUID id);

    @Query("SELECT * FROM " + CrimeDbSchema.CrimeTable.NAME)
    LiveData<List<Crime>> getCrimes();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addCrime(Crime crime);

    @Update
    void updateCrime(Crime crime);

    @Delete
    void deleteCrime(Crime crime);
}
