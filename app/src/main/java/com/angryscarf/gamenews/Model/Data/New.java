package com.angryscarf.gamenews.Model.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Jaime on 6/3/2018.
 */

@Entity
public class New {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;

    private String cover;


    private String date;

    private String description;

    private String body;

    private String game;

    private boolean favorite;


    public New(@NonNull String id, String title, String cover, String date, String description, String body, String game, boolean favorite) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.date = date;
        this.description = description;
        this.body = body;
        this.game = game;
        this.favorite = favorite;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public String getGame() {
        return game;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
