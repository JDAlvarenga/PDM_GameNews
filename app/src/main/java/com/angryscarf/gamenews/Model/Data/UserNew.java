package com.angryscarf.gamenews.Model.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Created by Jaime on 6/3/2018.
 */

@Entity(
        primaryKeys = {"user_id", "new_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"),
                @ForeignKey(entity = New.class, parentColumns = "id", childColumns = "new_id")
        }
)
public class UserNew {

    @NonNull
    private String user_id;

    @NonNull
    private String new_id;

    public UserNew(String user_id, String new_id) {
        this.user_id = user_id;
        this.new_id = new_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNew_id() {
        return new_id;
    }
}
