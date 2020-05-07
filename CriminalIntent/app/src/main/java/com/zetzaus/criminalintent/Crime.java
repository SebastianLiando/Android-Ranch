package com.zetzaus.criminalintent;

import com.zetzaus.criminalintent.database.CrimeDbSchema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class is an entity class that holds information of a crime.
 */
@Entity(tableName = CrimeDbSchema.CrimeTable.NAME)
public class Crime {

    @PrimaryKey
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.UUID)
    @NonNull
    private UUID mId;

    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.TITLE)
    private String mTitle;
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.SUSPECT)
    private String mSuspect;
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.PHONE)
    private String mSuspectNum;
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.DATE)
    private Date mDate;
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.SOLVED)
    private boolean mSolved;
    @ColumnInfo(name = CrimeDbSchema.CrimeTable.CrimeColumn.REQUIRES_POLICE)
    private boolean mRequiresPolice;

    /**
     * Creates a new crime with random ID.
     */
    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    /**
     * Creates a new crime with the given id.
     *
     * @param id the id of the crime.
     */
    @Ignore
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    /**
     * Returns the ID of the crime.
     *
     * @return the ID of the crime.
     */
    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    /**
     * Returns the title of the crime.
     *
     * @return the title of the crime.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the title of the crime.
     *
     * @param title the title of the crime to set.
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Returns the date of the crime as a <code>Date</code> object.
     *
     * @return the date of the crime.
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Returns the date of the crime as a <code>String</code> object.
     * The format depends on the locale of the device.
     *
     * @return the date of the crime.
     */
    public String getDateString() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        return format.format(mDate);
    }

    /**
     * Returns the time of the crime as a <code>String</code> object.
     * The example format is 23:59.
     *
     * @return the time of the crime.
     */
    public String getTimeString() {
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(mDate);
    }

    /**
     * Sets the date of the crime.
     *
     * @param date the date of the crime.
     */
    public void setDate(Date date) {
        mDate = date;
    }

    /**
     * Returns true if the crime has been solved.
     *
     * @return true if the crime has been solved.
     */
    public boolean isSolved() {
        return mSolved;
    }

    /**
     * Sets whether the crime is solved.
     *
     * @param solved true if the crime is solved.
     */
    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    /**
     * Returns true if the crime requires police assistant.
     *
     * @return true if the crime requires police assistant.
     */
    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    /**
     * Sets whether the crime requires police assistant.
     *
     * @param requiresPolice true if the crime requires police assistant.
     */
    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    /**
     * Returns the crime's suspect name.
     *
     * @return the crime's suspect name.
     */
    public String getSuspect() {
        return mSuspect;
    }

    /**
     * Sets the crime's suspect name.
     *
     * @param suspect
     */
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    /**
     * Returns the suspect's phone number.
     *
     * @return the suspect's phone number.
     */
    public String getSuspectNum() {
        return mSuspectNum;
    }

    /**
     * Sets the suspect's phone number.
     *
     * @param suspectNum the suspect's phone number.
     */
    public void setSuspectNum(String suspectNum) {
        mSuspectNum = suspectNum;
    }

    /**
     * Returns the file name for the photo of the crime.
     *
     * @return the file name for the photo of the crime.
     */
    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Crime)) return false;

        Crime otherCrime = (Crime) obj;

        if ((getTitle() == null && otherCrime.getTitle() != null) ||
                (getSuspect() == null && otherCrime.getSuspect() != null) ||
                (getSuspectNum() == null && otherCrime.getSuspectNum() != null)) {
            return false;
        }

        return getId().equals(otherCrime.getId()) &&
                Objects.equals(getTitle(), otherCrime.getTitle()) &&
                isSolved() == otherCrime.isSolved() &&
                isRequiresPolice() == otherCrime.isRequiresPolice() &&
                Objects.equals(getSuspect(), otherCrime.getSuspect()) &&
                Objects.equals(getSuspectNum(), otherCrime.getSuspectNum()) &&
                getDate().equals(otherCrime.getDate());
    }
}
