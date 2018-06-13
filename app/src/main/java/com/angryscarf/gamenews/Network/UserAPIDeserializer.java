package com.angryscarf.gamenews.Network;

import android.util.Log;

import com.angryscarf.gamenews.Model.Network.UserAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Jaime on 6/4/2018.
 */

public class UserAPIDeserializer  implements JsonDeserializer<UserAPI>{
    @Override
    public UserAPI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        UserAPI userAPI = new UserAPI();
        userAPI.favoriteNews = new ArrayList<>();

        JsonObject userObject = json.getAsJsonObject();

        //Extract News id
        JsonArray favorites = userObject.get("favoriteNews").getAsJsonArray();
        for (JsonElement favorite : favorites) {
                userAPI.favoriteNews.add(getString(favorite));
        }

        userAPI._id = getString(userObject.get("_id"));
        userAPI.user = getString(userObject.get("user"));
        userAPI.avatar = getString(userObject.get("avatar"));
        userAPI.password = getString(userObject.get("password"));
        userAPI.created_date = getString(userObject.get("created_date"));

        return userAPI;
    }
    //Check for null elements
    private String getString(JsonElement elm) {
        return elm != null ? elm.getAsString() : null;
    }
}
