package com.zetzaus.photogallery.api;

import android.net.Uri;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoInterceptor implements Interceptor {

    private static final String API_KEY = "d12d3dc7c5c2831784ed459a5a020495";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        HttpUrl url = originalRequest.url().newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .addQueryParameter("safe_search", "1")
                .build();

        Request newRequest = originalRequest.newBuilder()
                .url(url)
                .build();

        return chain.proceed(newRequest);
    }
}
