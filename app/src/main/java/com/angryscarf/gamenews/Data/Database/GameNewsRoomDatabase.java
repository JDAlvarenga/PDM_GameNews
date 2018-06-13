package com.angryscarf.gamenews.Data.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.Model.Data.User;
import com.angryscarf.gamenews.Model.Data.UserNew;

/**
 * Created by Jaime on 6/3/2018.
 */

@Database(entities = {New.class, Player.class}, version = 1)
public abstract class GameNewsRoomDatabase extends RoomDatabase{

    private static GameNewsRoomDatabase INSTANCE;
    private static String DATABASE_NAME = "game_news_database";

    public static GameNewsRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null) {
            //If null sync the class (first call)
            synchronized (GameNewsRoomDatabase.class) {
                if(INSTANCE == null) {
                    //Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GameNewsRoomDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract GameNewsDao gameNewsDao();



}
