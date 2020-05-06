package com.zetzaus.criminalintent.database;

import com.zetzaus.criminalintent.Crime;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Crime.class}, version = 1, exportSchema = false)
@TypeConverters(CrimeTypeConverter.class)
public abstract class CrimeDatabase extends RoomDatabase {

    public abstract CrimeDao crimeDao();

}
