package com.angryscarf.gamenews.Util;

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
    //TODO: move to resources
    public static String getGameName(String game) {
        switch (game) {
            case "lol":
                return "League of Legends";

            case "csgo":
                return "CS: Global Offensive";

            case "overwatch":
                return "Overwatch";

            default:
                return "No Game";
        }
    }
}
