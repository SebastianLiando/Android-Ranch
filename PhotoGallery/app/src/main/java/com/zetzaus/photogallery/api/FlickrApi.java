package com.zetzaus.photogallery.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {

    @GET(
            "services/rest/?method=flickr.interestingness.getList" +
                    "&api_key=d12d3dc7c5c2831784ed459a5a020495" +
                    "&format=json" +
                    "&nojsoncallback=1" +
                    "&extras=url_s"
    )
    Call<PhotoResponse> fetchPhotos(@Query("page") int page);
}
