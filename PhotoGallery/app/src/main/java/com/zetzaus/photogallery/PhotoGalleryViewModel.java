package com.zetzaus.photogallery;

import com.zetzaus.photogallery.api.GalleryItemDataSourceFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class PhotoGalleryViewModel extends ViewModel {

    private LiveData<PagedList<GalleryItem>> mLiveDataGalleryItems;
//    private DataSource<Integer, GalleryItem> mDataSource;

    public PhotoGalleryViewModel() {
        GalleryItemDataSourceFactory factory = new GalleryItemDataSourceFactory();
//        mDataSource = factory.create();  --> if reference to the DataSource is needed

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(100)
                .build();

        mLiveDataGalleryItems = (new LivePagedListBuilder<>(factory, config)).build();
    }

    public LiveData<PagedList<GalleryItem>> getLiveDataGalleryItems() {
        return mLiveDataGalleryItems;
    }
}
