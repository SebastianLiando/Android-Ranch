package com.zetzaus.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a custom <code>SQLiteOpenHelper</code> for the crime's database.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    /**
     * Creates a <code>CrimeBaseHelper</code>.
     *
     * @param context the application context.
     */
    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Creates the database table.
     *
     * @param db the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeDbSchema.CrimeTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CrimeDbSchema.CrimeTable.CrimeColumn.UUID + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.TITLE + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.SUSPECT + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.PHONE + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.DATE + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.SOLVED + ", " +
                CrimeDbSchema.CrimeTable.CrimeColumn.REQUIRES_POLICE + ")");
    }

    /**
     * Updates the database. Currently it does nothing.
     *
     * @param db         the database.
     * @param oldVersion the old version number.
     * @param newVersion the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
