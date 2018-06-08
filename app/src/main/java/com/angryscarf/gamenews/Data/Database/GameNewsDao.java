package com.angryscarf.gamenews.Data.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.User;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by Jaime on 6/3/2018.
 */

@Dao
public interface GameNewsDao {


    //---USERS---
   /*
   @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(User... users);

    @Delete
    void deleteUsers(User... users);

    @Query("SELECT * FROM user WHERE id = :uid")
    Flowable<User> getUserFlowableById(String uid);
*/
    //---News---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNews(New... news);

    @Delete
    void deleteNews(New... news);

    @Query("SELECT * FROM new WHERE id = :nid")
    Single<New> getNewById(String nid);

    @Query("SELECT * FROM new ORDER BY date DESC")
    Flowable<List<New>> getAllNewsFlowable();


    @Query("SELECT * FROM new WHERE game = :gameName")
    Flowable<List<New>> getNewsFlowableByGame(String gameName);

    @Query("UPDATE new SET favorite = :favorite WHERE id IN (:ids)")
    void updateFavotiteNews(boolean favorite, String... ids);

    @Query("UPDATE new SET favorite = :favorite WHERE id IN (:ids)")
    void updateFavotiteNews(boolean favorite, List<String> ids);

}
