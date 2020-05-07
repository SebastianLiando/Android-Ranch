package com.zetzaus.criminalintent.database;

import android.content.Context;
import android.util.Log;

import com.zetzaus.criminalintent.Crime;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Crime.class}, version = 2, exportSchema = false)
@TypeConverters(CrimeTypeConverter.class)
public abstract class CrimeDatabase extends RoomDatabase {

    public abstract CrimeDao crimeDao();

    private static final Migration mMigrationOneTwo = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.i("CrimeDatabase", "Entered migration");
        }
    };

    public static CrimeDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, CrimeDatabase.class, CrimeDbSchema.CrimeTable.NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(mMigrationOneTwo)
                .build();
    }

}
