package com.angryscarf.gamenews.Util;

import android.app.Application;
import android.content.res.Resources;

import com.angryscarf.gamenews.R;

/**
 * Created by Jaime on 6/16/2018.
 */

public class Util {
    public static String filterEmpty(String text, String def) {
        return text != null? text: def;
    }

    public static int getGameColorID(String game) {
        switch (game) {
            case "lol":
                return R.color.colorGameLoL;

            case "csgo":
                return R.color.colorGameCSGO;

            case "overwatch":
                return R.color.colorGameOW;

            default:
                return R.color.colorGameDefault;
        }
    }

    public static String getGameName(String game) {
        switch (game) {
            case "lol":
                return Resources.getSystem().getString(R.string.lol_game_name);

            case "csgo":
                return Resources.getSystem().getString(R.string.csgo_game_name);

            case "overwatch":
                return Resources.getSystem().getString(R.string.overwatch_game_name);

            default:
                return "No Game";
        }
    }
}
