package com.angryscarf.gamenews.Model.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Jaime on 6/3/2018.
 */

@Entity
public class User {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    public User(@NonNull String id, @NonNull String username, @NonNull String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }
}
