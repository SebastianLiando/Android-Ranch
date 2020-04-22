package com.zetzaus.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * This class is an entity class that holds information of a crime. The information includes a unique ID, a title,
 * the date of case, whether the crime is solved, and whether the crime is serious and therefore requires police
 * assistance.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    /**
     * Constructs a crime. This initializes the date to current date and generates a random ID.
     */
    public Crime() {
        mId = UUID.randomUUID();
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
     * The example format is Friday, Apr 13, 1920.
     *
     * @return the date of the crime.
     */
    public String getDateString() {
        DateFormat format = new SimpleDateFormat("EEEE, MMM dd, yyyy");
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
}
