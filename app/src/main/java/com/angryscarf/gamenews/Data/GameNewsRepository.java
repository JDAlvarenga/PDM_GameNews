package com.angryscarf.gamenews.Data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.angryscarf.gamenews.Data.Database.GameNewsDao;
import com.angryscarf.gamenews.Data.Database.GameNewsRoomDatabase;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.Model.Network.Authentication;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Network.NewAPI;
import com.angryscarf.gamenews.Model.Network.PlayerAPI;
import com.angryscarf.gamenews.Model.Network.ResponseAddFavorite;
import com.angryscarf.gamenews.Model.Network.UserAPI;
import com.angryscarf.gamenews.Network.AuthenticationDeserializer;
import com.angryscarf.gamenews.Network.GameNewsAPI;
import com.angryscarf.gamenews.Network.NewAPIDeserializer;
import com.angryscarf.gamenews.Network.PlayerAPIDeserializer;
import com.angryscarf.gamenews.Network.ResponseAddFavoriteDeserializer;
import com.angryscarf.gamenews.Network.RetrofitClient;
import com.angryscarf.gamenews.Network.UserAPIDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jaime on 6/3/2018.
 */

public class GameNewsRepository{

    private Application application;

    private GameNewsDao mDao;
    private Flowable<List<New>> mAllNewsFlowable;
    private Flowable<List<Player>> mAllPlayersFlowable;

    private GameNewsAPI gameNewsAPI;

    private static final String SHARED_PREFERENCES_FILE_NAME = "com.angryscarf.gamenews.LAST_USER";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID_KEY = "user_id";
    private static final String LOGGED_IN_KEY = "logged_in";
    private SharedPreferences preferences;


    private String token;
    private String userId;
    private boolean loggedIn;
    private List<String> favoriteNewIds;


    public GameNewsRepository(Application application) {
        this.application = application;
        GameNewsRoomDatabase db = GameNewsRoomDatabase.getDatabase(application);
        mDao = db.gameNewsDao();
        mAllNewsFlowable = mDao.getAllNewsFlowable();
        mAllPlayersFlowable = mDao.getAllPlayersFlowable();
        createGameNewsAPI();

        preferences = application.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        token = preferences.getString(TOKEN_KEY,null);
        userId = preferences.getString(USER_ID_KEY,null);
        loggedIn = preferences.getBoolean(LOGGED_IN_KEY, false);

        Log.d("REPO" ,"DEBUG: Loaded from SharedPref: token = "+token);
        Log.d("REPO" ,"DEBUG: Loaded from SharedPref: userId = "+userId);
        Log.d("REPO" ,"DEBUG: Loaded from SharedPref: loggedIn = "+loggedIn);

        if(loggedIn) {
            updateAllNews();
            updateAllPlayers();
        }

        //load favorites list from database into local variable
        Flowable<List<String>> favs = getFavoriteNewsFlowable()
                .doOnNext(strings -> favoriteNewIds = strings);
        //do it
        favs.subscribe();
        //the first time also sync with the API
        favs.first(new ArrayList<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(strings -> {
                    SyncWithAPI(false);
                });

    }

    public Flowable<List<New>> getAllNewsFlowable() {
        return  mAllNewsFlowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Player>> getAllPlayersFlowable() {
        return  mAllPlayersFlowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Completable saveNews(final New... news) {
        return Completable.fromCallable(() -> {
            mDao.insertNews(news);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable savePlayers(final Player... players) {
        return Completable.fromCallable(() -> {
            mDao.insertPlayers(players);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    //API Interaction

    //get token from API
    public Single<String> login(String user, String password) {
        SyncWithAPI(false);
        if(loggedIn || !isConnected()) return null;
        return gameNewsAPI.login(user, password)
                //save token on repository
                .doOnSuccess(authentication -> {
                    saveTokenVariable(authentication.token);
                    saveLogInVariable(true);
                    updateAllNews().subscribe(() -> {
                        SyncWithAPI(true);
                    });
                    updateAllPlayers();

                })
                .doOnError(throwable -> handleRequestError(throwable))
                //map to return token Single<String> only
                .map(authentication -> authentication.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void logout() {
        preferences.edit()
                .putString(TOKEN_KEY, token)
                .putString(USER_ID_KEY, userId)
                .putBoolean(LOGGED_IN_KEY, false)
        .apply();
        loggedIn = false;
        SyncWithAPI(false);
    }

    private void saveLogInVariable(boolean logged) {
        this.loggedIn = logged;
        preferences.edit()
                .putBoolean(LOGGED_IN_KEY, logged)
        .apply();
    }
    private void saveTokenVariable(String token) {
        this.token = token;
        preferences.edit()
                .putString(TOKEN_KEY, token)
        .apply();

    }
    private void saveUserIdVariable(String userId) {
        this.userId = userId;
        preferences.edit()
                .putString(USER_ID_KEY, userId)
        .apply();
    }


    public Single<UserAPI> fetchLoggedUserData() {
        if(!loggedIn || !isConnected()) return null;
        return gameNewsAPI.fetchLoggedUserData()
                .doOnSuccess(userAPI -> {
                    saveUserIdVariable(userAPI._id);
                })
                .doOnError(throwable -> handleRequestError(throwable))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Completable updateAllNews() {
        if(!loggedIn || !isConnected()) return null;
        Log.d("REPO", "DEBUG: CALLED updateAllNews");
        Single<List<NewAPI>> APINews =  gameNewsAPI.fetchAllNews()
                .doOnError(throwable -> handleRequestError(throwable));
                //map to array
                APINews.map(newAPIS -> {
                    New[] news = new New[newAPIS.size()];
                    for (int i = 0; i < newAPIS.size(); i++) {
                        NewAPI newAPI = newAPIS.get(i);
                        news[i] = new New(newAPI._id, newAPI.title, newAPI.coverImage, newAPI.created_date, newAPI.description, newAPI.body, newAPI.game,
                                favoriteNewIds.contains(newAPI._id));
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
                                Log.d("REPO", "DEBUG: Completed save news to databse");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //News were not saved
                                Log.d("REPO", "DEBUG: Failed to save news to database");

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleRequestError(e);
                    }
                });
                return APINews.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toCompletable();
    }

    public Completable updateAllPlayers() {
        if(!loggedIn || !isConnected()) return null;
        Log.d("REPO", "DEBUG: CALLED updateAllPlayers");
        Single<List<PlayerAPI>> APIPlayers =  gameNewsAPI.fetchAllPlayers()
                .doOnError(throwable -> handleRequestError(throwable));
                //map to array
                APIPlayers.map(playerAPIS -> {
                    Player[] players = new Player[playerAPIS.size()];
                    for (int i = 0; i < playerAPIS.size(); i++) {
                        PlayerAPI playerAPI = playerAPIS.get(i);
                        players[i] = new Player(playerAPI._id, playerAPI.name, playerAPI.bio, playerAPI.avatar, playerAPI.game);
                    }
                    return players;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                //pass array to room database
                .subscribe(new SingleObserver<Player[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Player[] players) {

                        savePlayers(players).subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onComplete() {
                                //News were saved
                                Log.d("REPO", "DEBUG: Completed save players from API");
                            }

                            @Override
                            public void onError(Throwable e) {
                                //News were not saved
                                Log.d("REPO", "DEBUG: Failed to save players from API");

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleRequestError(e);
                    }
                });
                return APIPlayers.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toCompletable();
    }


    public void updateFavoriteNews(boolean favorite, String... ids) {
        saveFavoriteNews(favorite, ids)
                .subscribe(() -> SyncWithAPI(false));
    }
    public void updateFavoriteNews(boolean favorite, List<String> ids) {
        saveFavoriteNews(favorite, ids)
                .subscribe(() -> SyncWithAPI(false));
    }

    public Completable saveFavoriteNews(boolean favorite, String... ids) {
        return Completable.fromCallable(() -> {
            mDao.updateFavotiteNews(favorite, ids);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
    public Completable saveFavoriteNews(boolean favorite, List<String> ids) {
        return Completable.fromCallable(() -> {
            mDao.updateFavotiteNews(favorite, ids);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }



    private Flowable<List<String>> getFavoriteNewsFlowable() {
        return mDao.getFavoriteNewsId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public void SyncWithAPI(boolean TruthSourceAPI) {
        Log.d("REPO" ,"DEBUG: Called SyncWithAPI. TruthSourceAPI = "+TruthSourceAPI);

        if(!isConnected()) {
            Log.d("REPO", "DEBUG: Not connected, stopped sync");
            return;
        }

        if(token != null) {
            Log.d("REPO" ,"DEBUG: Attempting to sync, token != null");
            /*
            Tries to upload favorite news saved on database to API
            Checks for favorites on database that are not into the API
            */
            fetchLoggedUserData()
                    .observeOn(Schedulers.io())
                    .subscribe(userAPI -> {
                        Log.d("REPO" ,"DEBUG: Fetched user data from API");
                        Log.d("REPO" ,"DEBUG: Fetched Favorites: "+userAPI.favoriteNews.size());
                        Log.d("REPO" ,"DEBUG: Saved Favorites: "+favoriteNewIds.size());

                        //Favorites in DB that aren't in API
                        for (String newId : favoriteNewIds) {
                            if(!userAPI.favoriteNews.contains(newId)) {
                                Log.d("REPO", "DEBUG: ");

                                if(TruthSourceAPI) {
                                    //save to DB (remove fav)
                                    saveFavoriteNews(false, newId)
                                    .subscribe();
                                }
                                else {
                                    //save to API
                                    gameNewsAPI.addNewAsFavorite(userId, newId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(Schedulers.io())
                                            .doOnError(throwable -> handleRequestError(throwable))
                                            .subscribe();
                                }

                            }
                        }

                       //Favorites in API that are not in DB
                       for (String newId : userAPI.favoriteNews) {
                           if(!favoriteNewIds.contains(newId)) {
                               if(TruthSourceAPI) {
                                   //save on DB (Add fav)
                                   saveFavoriteNews(true, newId)
                                   .subscribe();
                               }
                               else {
                                   //remove from API
                                   gameNewsAPI.removeNewAsFavorite(userId, newId)
                                           .subscribeOn(Schedulers.io())
                                           .observeOn(Schedulers.io())
                                           .doOnError(throwable -> handleRequestError(throwable))
                                           .subscribe();
                               }
                           }
                       }

            });

        }


    }

    //TODO: handle No internet connection
    private void handleRequestError(Throwable e) {
        if(e instanceof IOException) {
            //No Connectivity
        }
        else {
            Log.e("REPO", "ERROR ON REQUEST: ", e);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void createGameNewsAPI() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(UserAPI.class, new UserAPIDeserializer())
                .registerTypeAdapter(Authentication.class, new AuthenticationDeserializer())
                .registerTypeAdapter(NewAPI.class, new NewAPIDeserializer())
                .registerTypeAdapter(PlayerAPI.class, new PlayerAPIDeserializer())
                .registerTypeAdapter(ResponseAddFavorite.class, new ResponseAddFavoriteDeserializer())
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

        })
                .build();




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GameNewsAPI.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        gameNewsAPI = retrofit.create(GameNewsAPI.class);

    }



}



/*-----JavaScript Queries-----
* function getToken(user, password) {
    let req = new XMLHttpRequest();
    req.open('POST', 'http://gamenewsuca.herokuapp.com/login', true);
    req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    req.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200) {
            window.token = JSON.parse(req.responseText).token;
            console.log('Saved token');
        }
        else if(this.readyState == 4 && this.status != 200){
            console.log('Could not get token');
            console.log(req.responseText);
        }
    }
    req.send(`user=${user}&password=${password}`);
}

function getUser(token) {
    let req = new XMLHttpRequest();
    req.open('GET', `http://gamenewsuca.herokuapp.com/users/detail?_=${new Date().getTime()}`, true);
    req.setRequestHeader('Authorization', `Bearer ${token}`);
    req.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200) {
            window.user = JSON.parse(req.responseText);
            console.log('Fetched user');
        }
        else if(this.readyState == 4 && this.status != 200) {
            console.log('Could not get user details');
            console.log(req.responseText);
        }
    }
    req.send();
}


function getPlayersList(token) {
    let req = new XMLHttpRequest();
    req.open('GET', `http://gamenewsuca.herokuapp.com/players?_=${new Date().getTime()}`, true);
    req.setRequestHeader('Authorization', `Bearer ${token}`);
    req.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200) {
            window.players = JSON.parse(req.responseText);
            console.log('Fetched players list');
        }
        else if(this.readyState == 4 && this.status != 200) {
            console.log('Could not get players list');
            console.log(req.responseText);
        }
    }
    req.send();
}
* */
