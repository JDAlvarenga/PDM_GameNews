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

        playerAPI._id = playerObject.get("_id").getAsString();
        playerAPI.avatar = playerObject.get("avatar").getAsString();
        playerAPI.name = playerObject.get("name").getAsString();
        playerAPI.bio = playerObject.get("biografia").getAsString();
        playerAPI.game = playerObject.get("game").getAsString();
        return playerAPI;
    }
}
