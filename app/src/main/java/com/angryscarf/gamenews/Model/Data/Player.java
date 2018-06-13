package com.angryscarf.gamenews.Model.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Jaime on 6/11/2018.
 */

@Entity
public class Player {
    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String bio;
    private String avatar;
    private String game;

    public Player(@NonNull String id, String name, String bio, String avatar, String game) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.avatar = avatar;
        this.game = game;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getGame() {
        return game;
    }
}
