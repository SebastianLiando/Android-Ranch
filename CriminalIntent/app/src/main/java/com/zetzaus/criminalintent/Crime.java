package com.zetzaus.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * This class is an entity class that holds information of a crime.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private String mSuspect;
    private String mSuspectNum;
    private Date mDate;
    private boolean mSolved;
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
}
