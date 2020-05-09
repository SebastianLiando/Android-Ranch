package com.zetzaus.photogallery.api;

import android.content.Context;
import android.util.Log;

import com.zetzaus.photogallery.FlickrRepository;
import com.zetzaus.photogallery.GalleryItem;
import com.zetzaus.photogallery.QueryPreferences;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryItemDataSource extends PageKeyedDataSource<Integer, GalleryItem> {

    private static final String TAG = GalleryItemDataSource.class.getSimpleName();

    private FlickrApi mFlickrApi;
    private Context mContext;
    private int mMaxPage;
    String mQuery;

    public GalleryItemDataSource(Context context) {
        mFlickrApi = FlickrRepository.getFlickrApi();
        mContext = context.getApplicationContext();
        mQuery = QueryPreferences.getStoredQuery(mContext);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, GalleryItem> callback) {
        if (mQuery == null) {
            loadFirst(mFlickrApi.fetchPhotos(1), callback);
        } else {
            loadFirst(mFlickrApi.searchPhotos(mQuery, 1), callback);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GalleryItem> callback) {
        Integer prevPage = params.key == 1 ? null : params.key - 1;

        if (mQuery == null) {
            loadPrev(prevPage, mFlickrApi.fetchPhotos(params.key), callback);
        } else {
            loadPrev(prevPage, mFlickrApi.searchPhotos(mQuery, params.key), callback);
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GalleryItem> callback) {
        Integer nextPage = params.key == mMaxPage ? null : params.key + 1;

        if (mQuery == null) {
            loadNext(nextPage, mFlickrApi.fetchPhotos(params.key), callback);
        } else {
            loadNext(nextPage, mFlickrApi.searchPhotos(mQuery, params.key), callback);
        }

    }

    private void loadFirst(Call<PhotoResponse> responseCall, LoadInitialCallback<Integer, GalleryItem> callback) {
        responseCall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Log.d(TAG, "loadInitial onResponse()");
                callback.onResult(response.body().getGalleryItems(), null, 2);
                mMaxPage = response.body().getNumOfPages();
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.d(TAG, "loadInitial onFailure()");
            }
        });
    }

    private void loadNext(Integer nextKey, Call<PhotoResponse> responseCall, LoadCallback<Integer, GalleryItem> callback) {
        load(nextKey, responseCall, callback);
    }

    private void loadPrev(Integer nextKey, Call<PhotoResponse> responseCall, LoadCallback<Integer, GalleryItem> callback) {
        load(nextKey, responseCall, callback);
    }

    private void load(Integer pageToLoad, Call<PhotoResponse> responseCall, LoadCallback<Integer, GalleryItem> callback) {
        responseCall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Log.d(TAG, "loadAfter onResponse()");
                callback.onResult(response.body().getGalleryItems(), pageToLoad);
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.d(TAG, "loadAfter onFailure()");
            }
        });
    }

}
