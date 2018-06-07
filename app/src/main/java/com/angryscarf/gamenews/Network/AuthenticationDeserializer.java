package com.angryscarf.gamenews.Network;

import com.angryscarf.gamenews.Model.Network.Authentication;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Jaime on 6/4/2018.
 */

public class AuthenticationDeserializer implements JsonDeserializer{
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Authentication auth = new Authentication();

        JsonObject authObject = json.getAsJsonObject();
        auth.token = authObject.get("token").getAsString();
        return auth;
    }
}
