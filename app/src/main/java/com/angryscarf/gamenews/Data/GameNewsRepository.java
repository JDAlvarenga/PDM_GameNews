package com.angryscarf.gamenews.Data;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.angryscarf.gamenews.Data.Database.GameNewsDao;
import com.angryscarf.gamenews.Data.Database.GameNewsRoomDatabase;
import com.angryscarf.gamenews.Model.Network.Authentication;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.User;
import com.angryscarf.gamenews.Model.Network.NewAPI;
import com.angryscarf.gamenews.Model.Network.UserAPI;
import com.angryscarf.gamenews.Network.AuthenticationDeserializer;
import com.angryscarf.gamenews.Network.GameNewsAPI;
import com.angryscarf.gamenews.Network.NewAPIDeserializer;
import com.angryscarf.gamenews.Network.UserAPIDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jaime on 6/3/2018.
 */

public class GameNewsRepository{

    private GameNewsDao mDao;
    private Flowable<List<New>> mAllNewsFlowable;

    private GameNewsAPI gameNewsAPI;


    private String token;


    public GameNewsRepository(Application application) {
        GameNewsRoomDatabase db = GameNewsRoomDatabase.getDatabase(application);
        mDao = db.gameNewsDao();
        mAllNewsFlowable = mDao.getAllNewsFlowable();

        createGameNewsAPI();

    }

    public Flowable<List<New>> getAllNewsFlowable() {
        return  mAllNewsFlowable;
    }

    public Completable saveNews(final New... news) {
        return Completable.fromCallable(() -> {
            mDao.insertNews(news);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    //API Interaction

    //get token from API
    public Single<String> login(String user, String password) {
        return gameNewsAPI.login(user, password)
                //save token on repository
                .doOnSuccess(authentication -> {
                    setToken(authentication.token);
                })
                //map to return token Single<String> only
                .map(authentication -> authentication.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void logout() {
        setToken(null);
    }

    public void setToken (String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public Single<UserAPI> fetchLoggedUserData() {
        return gameNewsAPI.fetchLoggedUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    //TODO: Create Hot Observables to avoid subscribing
    public Completable updateAllNews() {
        Log.d("REPO", "DEBUG: CALLED updateAllNews");
        Single<List<NewAPI>> APINews =  gameNewsAPI.fetchAllNews();
                //map to array
                APINews.map(newAPIS -> {
                    New[] news = new New[newAPIS.size()];
                    for (int i = 0; i < newAPIS.size(); i++) {
                        NewAPI newAPI = newAPIS.get(i);
                        news[i] = new New(newAPI._id, newAPI.title, newAPI.coverImage, newAPI.created_date, newAPI.description, newAPI.body, newAPI.game);
                    }
                    return news;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                //pass array to room database
                .subscribe(new SingleObserver<New[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(New[] news) {

                        saveNews(news).subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onComplete() {
                                //News were saved
                                Log.d("REPO", "DEBUG: Completed write to database");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //News were not saved
                                Log.d("REPO", "DEBUG: Failed to write to database");

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("REPO", "ERROR getNEWS");
                        e.printStackTrace();
                    }
                });
                return APINews.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toCompletable();
    }




    private void createGameNewsAPI() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(UserAPI.class, new UserAPIDeserializer())
                .registerTypeAdapter(Authentication.class, new AuthenticationDeserializer())
                .registerTypeAdapter(NewAPI.class, new NewAPIDeserializer())
                .create();


        OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                //Add headers
                Request.Builder builder = request.newBuilder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded");

                //Add token
                if(token != null && !token.isEmpty()) {
                            builder = builder.addHeader("Authorization", "Bearer " + token);
                }
                request = builder.build();

                Response response = chain.proceed(request);
                return response;
            }
        }).build();




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GameNewsAPI.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        gameNewsAPI = retrofit.create(GameNewsAPI.class);

    }

}
