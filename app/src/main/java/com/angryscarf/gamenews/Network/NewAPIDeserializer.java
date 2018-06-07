package com.angryscarf.gamenews.Network;

import com.angryscarf.gamenews.Model.Network.NewAPI;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Jaime on 6/4/2018.
 */

public class NewAPIDeserializer implements JsonDeserializer{
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        NewAPI newAPI = new NewAPI();
        JsonObject newObject = json.getAsJsonObject();

        newAPI._id = getString(newObject.get("_id"));
        newAPI.title = getString(newObject.get("title"));
        newAPI.body = getString(newObject.get("body"));
        newAPI.game = getString(newObject.get("game"));
        newAPI.coverImage = getString(newObject.get("coverImage"));
        newAPI.created_date = getString(newObject.get("created_date"));
        newAPI.description = getString(newObject.get("description"));
        return newAPI;
    }

    //Check for null elements
    private String getString(JsonElement elm) {
        return elm != null ? elm.getAsString() : null;
    }
}
