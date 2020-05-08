package com.zetzaus.photogallery.api;

import com.google.gson.annotations.SerializedName;
import com.zetzaus.photogallery.GalleryItem;

import java.util.List;

public class PhotoResponse {
    @SerializedName("photo")
    private List<GalleryItem> mGalleryItems;
    @SerializedName("pages")
    private int mNumOfPages;

    public List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void setGalleryItems(List<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
    }

    public int getNumOfPages() {
        return mNumOfPages;
    }

    public void setNumOfPages(int numOfPages) {
        mNumOfPages = numOfPages;
    }
}
