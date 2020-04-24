package com.zetzaus.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeDbSchema.CrimeTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CrimeDbSchema.CrimeTable.CrimeColumn.UUID + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.TITLE + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.DATE + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.SOLVED + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.REQUIRES_POLICE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
