package com.zetzaus.locatr;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

/**
 * This class is a model class for a single image.
 */
public class GalleryItem {
    @SerializedName("title")
    private String mCaption;

    @SerializedName("id")
    private String mId;

    @SerializedName("url_s")
    private String mURL;

    @SerializedName("owner")
    private String mOwner;

    /**
     * Returns the caption.
     *
     * @return the caption.
     */
    @NonNull
    @Override
    public String toString() {
        return mCaption;
    }

    /**
     * Returns the caption.
     *
     * @return the caption.
     */
    public String getCaption() {
        return mCaption;
    }

    /**
     * Sets the image caption.
     *
     * @param caption the image caption.
     */
    public void setCaption(String caption) {
        mCaption = caption;
    }

    /**
     * Returns the image id.
     *
     * @return the image id.
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the image id.
     *
     * @param id the image id.
     */
    public void setId(String id) {
        mId = id;
    }

    /**
     * Returns the image URL.
     *
     * @return the image URL.
     */
    public String getURL() {
        return mURL;
    }

    /**
     * Sets the image URL.
     *
     * @param URL the image URL.
     */
    public void setURL(String URL) {
        mURL = URL;
    }

    /**
     * Returns the image's owner id.
     *
     * @return the image's owner id.
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * Sets the image's owner id.
     *
     * @param owner the image's owner id.
     */
    public void setOwner(String owner) {
        mOwner = owner;
    }

    /**
     * Returns the Uri for the photo page in the Flickr website.
     *
     * @return the Uri for the photo page in the Flickr website.
     */
    public Uri getPhotoPageUri() {
        return Uri.parse("https://www.flickr.com/photos/").buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
}
