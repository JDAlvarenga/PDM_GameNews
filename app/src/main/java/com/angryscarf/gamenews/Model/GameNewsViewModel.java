package com.angryscarf.gamenews.Model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.angryscarf.gamenews.Data.GameNewsRepository;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.Model.Network.Authentication;
import com.angryscarf.gamenews.Model.Network.NewAPI;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by Jaime on 6/4/2018.
 */

public class GameNewsViewModel extends AndroidViewModel {
    private GameNewsRepository mRepository;

    private Flowable<List<New>> mAllNews;
    private Flowable<List<Player>> mAllPlayers;


    public GameNewsViewModel(@NonNull Application application) {
        super(application);

        mRepository = GameNewsRepository.getInstance(application);
        mAllNews = mRepository.getAllNewsFlowable();
        mAllPlayers = mRepository.getAllPlayersFlowable();

        
    }

    public Flowable<List<New>> getAllnews() {
        return mAllNews;
    }
    public Flowable<List<Player>> getAllplayers() {
        return mAllPlayers;
    }


    public Completable login(String user, String password) {
        return mRepository.login(user, password);
    }

    public void logout() {
        mRepository.logout();
    }


    public void toggleFavoriteNew(New aNew) {
        aNew.setFavorite(!aNew.isFavorite());
        mRepository.updateFavoriteNews(aNew.isFavorite(), aNew.getId());
    }

    public boolean isLoggedIn() {
        return mRepository.isLoggedIn();
    }

    public String getLoggedUserName() {
        return mRepository.getUserName();
    }

    public Flowable<Boolean> loggedInStatus() {
        return mRepository.isLoggedInStatus;
    }


}
