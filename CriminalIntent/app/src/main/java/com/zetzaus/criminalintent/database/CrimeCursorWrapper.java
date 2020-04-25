package com.zetzaus.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.zetzaus.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a <code>Crime</code> object from the cursor.
     *
     * @return the crime object.
     */
    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.UUID));
        UUID id = UUID.fromString(uuid);
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.TITLE));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.SUSPECT));
        String phone = getString(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.PHONE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.DATE));
        boolean isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.SOLVED)) == 1;
        boolean requiresPolice = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.CrimeColumn.REQUIRES_POLICE)) == 1;

        Crime crime = new Crime(id);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved);
        crime.setTitle(title);
        crime.setSuspect(suspect);
        crime.setRequiresPolice(requiresPolice);
        crime.setSuspectNum(phone);

        return crime;
    }
}
