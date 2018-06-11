package com.angryscarf.gamenews.Network;

import android.util.Log;

import com.angryscarf.gamenews.Model.Network.ResponseAddFavorite;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jaime on 6/9/2018.
 */

public class ResponseAddFavoriteDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Log.d("RES_ADD_DESERIALIZER" ,"DEBUG: response = "+json.getAsString());

        ResponseAddFavorite response = new ResponseAddFavorite();
        JsonObject resObject = json.getAsJsonObject();
        response.success = hadSuccess(resObject.get("success"));

        return response;
    }

    private boolean hadSuccess(JsonElement elm) {
        return elm.getAsBoolean();
        //return elm != null? elm.getAsBoolean() : false;
    }
}
