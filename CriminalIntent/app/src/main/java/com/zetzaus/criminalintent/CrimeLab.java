package com.zetzaus.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zetzaus.criminalintent.database.CrimeBaseHelper;
import com.zetzaus.criminalintent.database.CrimeCursorWrapper;
import com.zetzaus.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class is a singleton class that holds the list of crimes.
 */
public class CrimeLab {

    private static CrimeLab instance = null;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * Returns the reference to this class.
     *
     * @param context the context.
     * @return the reference to this class.
     */
    public static CrimeLab getInstance(Context context) {
        if (instance == null) {
            instance = new CrimeLab(context);
        }

        return instance;
    }

    /**
     * Returns the list of crimes.
     *
     * @return the list of crimes.
     */
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = simpleQuery(null, null);

        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return crimes;
    }

    /**
     * Returns the crime with the specific id.
     *
     * @param id the specific id.
     * @return the corresponding crime.
     */
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = simpleQuery(CrimeDbSchema.CrimeTable.CrimeColumn.UUID + "=?",
                new String[]{id.toString()});

        if (cursor.getCount() == 0) return null;

        try {
            cursor.moveToFirst();
            return cursor.getCrime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return null;
    }

    /**
     * Adds a new crime to the database.
     *
     * @param crime the new crime.
     */
    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    /**
     * Updates the given crime to the database.
     *
     * @param crime the updated crime.
     */
    public void updateCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        String uuid = crime.getId().toString();
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.CrimeColumn.UUID + " = ?", new String[]{uuid});
    }

    /**
     * Removes a crime from the database.
     *
     * @param crimeId the crime id to be removed.
     */
    public void deleteCrime(final UUID crimeId) {
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.CrimeColumn.UUID + " = ?",
                new String[]{crimeId.toString()});
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.SUSPECT, crime.getSuspect());
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.CrimeColumn.REQUIRES_POLICE, crime.isRequiresPolice() ? 1 : 0);
        return values;
    }

    private CrimeCursorWrapper simpleQuery(String where, String[] whereArgs) {
        return new CrimeCursorWrapper(
                mDatabase.query(CrimeDbSchema.CrimeTable.NAME,
                        null, where, whereArgs,
                        null, null, null)
        );
    }
}
