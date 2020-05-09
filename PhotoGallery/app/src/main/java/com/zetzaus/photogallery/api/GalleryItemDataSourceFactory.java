package com.zetzaus.photogallery.api;

import android.content.Context;

import com.zetzaus.photogallery.GalleryItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class GalleryItemDataSourceFactory extends DataSource.Factory<Integer, GalleryItem> {

    private MutableLiveData<GalleryItemDataSource> mSourceMutableLiveData = new MutableLiveData<>();
    private Context mContext;

    public GalleryItemDataSourceFactory(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public DataSource<Integer, GalleryItem> create() {
        GalleryItemDataSource source = new GalleryItemDataSource(mContext);
        mSourceMutableLiveData.postValue(source);
        return source;
    }

    public MutableLiveData<GalleryItemDataSource> getSourceMutableLiveData() {
        return mSourceMutableLiveData;
    }
}
