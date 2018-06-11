package com.angryscarf.gamenews.Network;

import com.angryscarf.gamenews.Model.Network.Authentication;
import com.angryscarf.gamenews.Model.Network.NewAPI;
import com.angryscarf.gamenews.Model.Network.ResponseAddFavorite;
import com.angryscarf.gamenews.Model.Network.UserAPI;
import com.google.gson.JsonElement;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jaime on 6/4/2018.
 */

public interface GameNewsAPI {

    String ENDPOINT = "https://gamenewsuca.herokuapp.com/";


    @POST("login")
    @FormUrlEncoded
    Single<Authentication> login(@Field("user") String user, @Field("password") String password);


    @GET("users/detail")
    Single<UserAPI> fetchLoggedUserData();


    @GET("news")
    Single<List<NewAPI>> fetchAllNews();

    @POST("users/{userId}/fav")
    @FormUrlEncoded
    Single<ResponseAddFavorite> addNewAsFavorite(@Path("userId") String userId, @Field("new") String newId);

    /*
    @DELETE("users/{userId}/fav")
    @FormUrlEncoded
    Completable removeNewAsFavorite(@Path("userId") String userId, @Field("new") String newId);
*/
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/users/{userId}/fav", hasBody = true)
    Completable removeNewAsFavorite(@Path("userId") String userId, @Field("new") String newId);


}
