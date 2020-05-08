package com.zetzaus.photogallery.api;

import com.zetzaus.photogallery.GalleryItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class GalleryItemDataSourceFactory extends DataSource.Factory<Integer, GalleryItem> {

    private MutableLiveData<GalleryItemDataSource> mSourceMutableLiveData = new MutableLiveData<>();

    @NonNull
    @Override
    public DataSource<Integer, GalleryItem> create() {
        GalleryItemDataSource source = new GalleryItemDataSource();
        mSourceMutableLiveData.postValue(source);
        return source;
    }
}
