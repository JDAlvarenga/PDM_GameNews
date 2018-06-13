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

        mRepository = new GameNewsRepository(application);
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
        Single<String> loginSingle = mRepository.login(user, password);

        if(loginSingle == null) {
            mRepository.updateAllNews();
            return Completable.complete();
        }
        loginSingle.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {
            }

            @Override
            public void onError(Throwable e) {
                Log.d("VIEW_HOLDER", "ERROR login", e);
            }
        });

        return loginSingle.toCompletable();

    }


    public void toggleFavoriteNew(New aNew) {
        mRepository.updateFavoriteNews(!aNew.isFavorite(), aNew.getId());
    }


}
