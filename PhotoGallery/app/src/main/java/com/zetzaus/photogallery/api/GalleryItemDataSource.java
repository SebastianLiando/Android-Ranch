package com.zetzaus.photogallery.api;

import android.util.Log;

import com.zetzaus.photogallery.FlickrRepository;
import com.zetzaus.photogallery.GalleryItem;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryItemDataSource extends PageKeyedDataSource<Integer, GalleryItem> {

    private static final String TAG = GalleryItemDataSource.class.getSimpleName();

    private FlickrApi mFlickrApi;
    private int mMaxPage;

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, GalleryItem> callback) {
        mFlickrApi = FlickrRepository.getFlickrApi();
        mFlickrApi.fetchPhotos(1).enqueue(new Callback<PhotoResponse>() {
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

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GalleryItem> callback) {
        Integer prevPage = params.key == 1 ? null : params.key - 1;

        mFlickrApi.fetchPhotos(params.key).enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Log.d(TAG, "loadBefore onResponse()");
                callback.onResult(response.body().getGalleryItems(), prevPage);
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.d(TAG, "loadBefore onFailure()");
            }
        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, GalleryItem> callback) {
        Integer nextPage = params.key == mMaxPage ? null : params.key + 1;

        mFlickrApi.fetchPhotos(params.key).enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Log.d(TAG, "loadAfter onResponse()");
                callback.onResult(response.body().getGalleryItems(),nextPage);
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.d(TAG, "loadAfter onFailure()");
            }
        });
    }
}
