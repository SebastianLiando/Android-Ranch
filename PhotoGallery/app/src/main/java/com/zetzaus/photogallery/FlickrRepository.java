package com.zetzaus.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zetzaus.photogallery.api.FlickrApi;
import com.zetzaus.photogallery.api.PhotoDeserializer;
import com.zetzaus.photogallery.api.PhotoInterceptor;
import com.zetzaus.photogallery.api.PhotoResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new PhotoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .client(client)
                .build();

        return retrofit.create(FlickrApi.class);
    }

    public LiveData<List<GalleryItem>> fetchPhotos() {
        return fetchResponseFor(mFlickrApi.fetchPhotos(1));
    }

    public LiveData<List<GalleryItem>> searchPhotos(String text) {
        return fetchResponseFor(mFlickrApi.searchPhotos(text, 1));
    }

    private LiveData<List<GalleryItem>> fetchResponseFor(Call<PhotoResponse> operation) {
        final MutableLiveData<List<GalleryItem>> content = new MutableLiveData<>();

        operation.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                content.postValue(response.body().getGalleryItems());
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.e(TAG, "Error response: ", t);
            }
        });

        return content;
    }

    @WorkerThread
    public Bitmap fetchImage(String url) {
        try {
            Response<ResponseBody> response = mFlickrApi.fetchUrlBytes(url).execute();
            InputStream stream = response.body().byteStream();
            Bitmap image = BitmapFactory.decodeStream(stream);
            stream.close();
            return image;
        } catch (IOException e) {
            Log.e(TAG, "Error when downloading image: ", e);
        }

        return null;
    }

}
