package com.zetzaus.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = FlickrFetchr.class.getSimpleName();
    private static final String API_KEY = "d12d3dc7c5c2831784ed459a5a020495";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        // Open Connection
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + " : with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(int page) {
        List<GalleryItem> galleryItems = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", Integer.toString(page))
                    .build().toString();

            String jsonString = getUrlString(url);
            parseItems(galleryItems, new JSONObject(jsonString));

            Log.i(TAG, "Received JSON:\n" + jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Error in getting url: " + e);
        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing JSON: " + e);
        }

        return galleryItems;
    }

    private void parseItems(List<GalleryItem> galleryItems, JSONObject jsonObject) throws JSONException {
        Gson gson = new Gson();

        // Navigate to the photos array
        JSONObject photos = jsonObject.getJSONObject("photos");
        JSONArray photosArray = photos.getJSONArray("photo");

        for (int i = 0; i < photosArray.length(); i++) {
            JSONObject object = photosArray.getJSONObject(i);
            GalleryItem item = gson.fromJson(object.toString(), GalleryItem.class);
            galleryItems.add(item);
        }
    }
}
