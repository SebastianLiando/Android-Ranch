package com.zetzaus.photogallery;

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

    @NonNull
    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public String getOwner() {
        return mOwner;
    }

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
