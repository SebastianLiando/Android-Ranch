package com.zetzaus.locatr;

import android.location.Location;
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

/**
 * This class handles the networking with Flickr's REST API.
 */
public class FlickrFetchr {

    private static final String TAG = FlickrFetchr.class.getSimpleName();
    private static final String API_KEY = "d12d3dc7c5c2831784ed459a5a020495";
    private static final String METHOD_RECENT_SEARCH = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s,geo")
            .build();

    /**
     * Returns a byte array for the data at the url page.
     *
     * @param urlSpec the url of the page.
     * @return a byte array containing the data.
     * @throws IOException if the url is null.
     */
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

    /**
     * Returns the <code>String</code> data of the page located at the url.
     *
     * @param urlSpec the url of the page.
     * @return the <code>String</code> of the page located at the url.
     * @throws IOException if the url is null.
     * @see FlickrFetchr#getUrlBytes(String)
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * Returns a list of <code>GalleryItem</code> for the recently posted photos in FLickr.
     *
     * @param page the page number.
     * @return a list of <code>GalleryItem</code>.
     */
    public List<GalleryItem> fetchRecentPhotos(int page) {
        String url = buildURL(METHOD_RECENT_SEARCH, "", page);
        return downloadGalleryItems(url);
    }

    /**
     * Returns a {@link List} of {@link GalleryItem} for the queried search in Flickr.
     *
     * @param query the query key.
     * @param page  the page number.
     * @return a list of <code>GalleryItem</code>.
     */
    public List<GalleryItem> searchPhotos(String query, int page) {
        String url = buildURL(METHOD_SEARCH, query, page);
        return downloadGalleryItems(url);
    }

    /**
     * Returns a {@link List} of {@link GalleryItem} from the given location.
     *
     * @param location the location.
     * @return a {@link List} of {@link GalleryItem}
     */
    public List<GalleryItem> searchPhotos(Location location) {
        String url = buildURL(location);
        return downloadGalleryItems(url);
    }

    /**
     * Returns the URL in <code>String</code>
     *
     * @param method the method to do.
     * @param text   the search query text. Not used in recent photos.
     * @param page   the page number. Not used in search method.
     * @return the URL.
     */
    private String buildURL(String method, String text, int page) {
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method)
                .appendQueryParameter("page", Integer.toString(page));

        if (method.equals(METHOD_SEARCH))
            builder.appendQueryParameter("text", text);

        return builder.build().toString();
    }

    /**
     * Returns the URL for images in the location.
     *
     * @param location the location.
     * @return the URL.
     */
    private String buildURL(Location location) {
        return ENDPOINT.buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude())
                .build().toString();
    }

    /**
     * Returns a list of <code>GalleryItem</code> object from the url by parsing the json file.
     *
     * @param url the url.
     * @return a list of <code>GalleryItem</code>.
     */
    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> galleryItems = new ArrayList<>();

        try {
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

    /**
     * Add items from the json file to the list.
     *
     * @param galleryItems the list to be added.
     * @param jsonObject   the JSON object file.
     * @throws JSONException if the parsing failed.
     */
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
