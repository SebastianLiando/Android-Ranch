package com.zetzaus.photogallery;

import android.app.Application;

import com.zetzaus.photogallery.api.GalleryItemDataSource;
import com.zetzaus.photogallery.api.GalleryItemDataSourceFactory;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class PhotoGalleryViewModel extends AndroidViewModel {

    private LiveData<PagedList<GalleryItem>> mLiveDataGalleryItems;
    private LiveData<GalleryItemDataSource> mDataSource;
    private MutableLiveData<String> mLiveDataQuery = new MutableLiveData<>();

    private GalleryItemDataSourceFactory mFactory;
    private PagedList.Config mConfig = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(100)
            .build();

    public PhotoGalleryViewModel(Application application) {
        super(application);
        mFactory = new GalleryItemDataSourceFactory(application.getApplicationContext());
        mFactory.create();
        mDataSource = mFactory.getSourceMutableLiveData();

        mLiveDataGalleryItems = Transformations.switchMap(mLiveDataQuery, new Function<String, LiveData<PagedList<GalleryItem>>>() {
            @Override
            public LiveData<PagedList<GalleryItem>> apply(String input) {
                mDataSource.getValue().invalidate();
                return new LivePagedListBuilder<>(mFactory, mConfig).build();
            }
        });

        // Load initial
        setPhotoQuery(QueryPreferences.getStoredQuery(application.getApplicationContext()));
    }

    public LiveData<PagedList<GalleryItem>> getLiveDataGalleryItems() {
        return mLiveDataGalleryItems;
    }

    public void setPhotoQuery(String query) {
        QueryPreferences.setStoredQuery(getApplication().getApplicationContext(), query);
        mLiveDataQuery.postValue(query);
    }
}
