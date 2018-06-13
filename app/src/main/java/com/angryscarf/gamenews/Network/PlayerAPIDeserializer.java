package com.angryscarf.gamenews.Network;

import com.angryscarf.gamenews.Model.Network.PlayerAPI;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jaime on 6/4/2018.
 */

public class PlayerAPIDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        PlayerAPI playerAPI = new PlayerAPI();

        JsonObject playerObject = json.getAsJsonObject();

        playerAPI._id = getString(playerObject.get("_id"));
        playerAPI.avatar = getString(playerObject.get("avatar"));
        playerAPI.name = getString(playerObject.get("name"));
        playerAPI.bio = getString(playerObject.get("biografia"));
        playerAPI.game = getString(playerObject.get("game"));
        return playerAPI;
    }
    //Check for null elements
    private String getString(JsonElement elm) {
        return elm != null ? elm.getAsString() : null;
    }
}
