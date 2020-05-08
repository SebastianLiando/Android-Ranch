package com.zetzaus.photogallery.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zetzaus.photogallery.GalleryItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhotoDeserializer implements JsonDeserializer<PhotoResponse> {
    @Override
    public PhotoResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        Gson gson = new Gson();
        JsonObject body = json.getAsJsonObject().getAsJsonObject("photos");
        return gson.fromJson(body, PhotoResponse.class);
    }
}
