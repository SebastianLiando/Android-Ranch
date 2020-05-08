package com.zetzaus.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zetzaus.photogallery.api.FlickrApi;
import com.zetzaus.photogallery.api.PhotoDeserializer;
import com.zetzaus.photogallery.api.PhotoResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrRepository {

    private static final String TAG = FlickrRepository.class.getSimpleName();
    private static final String API_KEY = "d12d3dc7c5c2831784ed459a5a020495";
    private static final Uri ENDPOINT = Uri.parse("services/rest/").buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("method", "flickr.interestingness.getList") //TODO: abstract
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    private FlickrApi mFlickrApi;

    public FlickrRepository() {
        mFlickrApi = getFlickrApi();
    }

    public static FlickrApi getFlickrApi() {
        Gson customGson = new GsonBuilder()
                .registerTypeAdapter(PhotoResponse.class, new PhotoDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .build();

        return retrofit.create(FlickrApi.class);
    }

    public LiveData<List<GalleryItem>> fetchPhotos() {
        final MutableLiveData<List<GalleryItem>> content = new MutableLiveData<>();

        Call<PhotoResponse> request = mFlickrApi.fetchPhotos(1);
        request.enqueue(new Callback<PhotoResponse>() {

            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                content.setValue(response.body().getGalleryItems());
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.e(TAG, "Error response: ", t);
            }
        });

        return content;
    }

}
