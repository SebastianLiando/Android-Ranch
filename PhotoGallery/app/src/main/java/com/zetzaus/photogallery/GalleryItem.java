package com.zetzaus.photogallery;

import androidx.annotation.NonNull;

/**
 * This class is a model class for a single image.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mURL;

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
}
