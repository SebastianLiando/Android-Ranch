package com.zetzaus.photogallery.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList")
    Call<PhotoResponse> fetchPhotos(@Query("page") int page);

    @GET("services/rest/?method=flickr.photos.search")
    Call<PhotoResponse> searchPhotos(@Query("text") String key, @Query("page") int page);

    @GET
    Call<ResponseBody> fetchUrlBytes(@Url String url);
}
