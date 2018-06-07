package com.angryscarf.gamenews.Network;

import com.angryscarf.gamenews.Model.Network.UserAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jaime on 6/4/2018.
 */

public class UserAPIDeserializer  implements JsonDeserializer<UserAPI>{
    @Override
    public UserAPI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        UserAPI userAPI = new UserAPI();

        JsonObject userObject = json.getAsJsonObject();

        //Extract News id
        JsonArray favorites = userObject.get("favoriteNews").getAsJsonArray();
        for (JsonElement favorite : favorites) {
            userAPI.favoriteNews.add(favorite.getAsJsonObject().get("_id").getAsString());
        }

        userAPI._id = userObject.get("_id").getAsString();
        userAPI.user = userObject.get("user").getAsString();
        userAPI.avatar = userObject.get("avatar").getAsString();
        userAPI.password = userObject.get("password").getAsString();
        userAPI.created_date = (userObject.get("created_date").getAsString());

        return userAPI;
    }
}
