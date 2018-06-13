package com.angryscarf.gamenews.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.angryscarf.gamenews.Fragments.Adapters.NewsAdapter;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.GameNewsViewModel;
import com.angryscarf.gamenews.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnNewsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements NewsAdapter.onNewsAdapterInteractionListener{
    //the fragment initialization parameters
    private static final String ARG_GAME = "arg_game";
    private static final String ARG_FAVORITE = "arg_favorite";

    //State variable keys
    private static final String STATE_GAME = "state_game";
    private static final String STATE_FAVORITE = "state_favorite";


    private String mGame;
    private boolean favorites;
    private OnNewsFragmentInteractionListener mListener;
    private RecyclerView recycler;
    private NewsAdapter adapter;
    private GameNewsViewModel viewModel;

    private Disposable currentSub;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param game Game to show
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment newInstance(String game, @NonNull boolean favorites) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME, game);
        args.putBoolean(ARG_FAVORITE, favorites);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame = getArguments().getString(ARG_GAME);
            favorites = getArguments().getBoolean(ARG_FAVORITE);
        }

        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        if (savedInstanceState != null) {
            mGame = savedInstanceState.getString(STATE_GAME);
            favorites = savedInstanceState.getBoolean(STATE_FAVORITE);
        }
        adapter = new NewsAdapter(this, null);

        setGameNews(mGame, favorites);


        recycler = v.findViewById(R.id.news_recycler);
        recycler.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this.getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if(position % 3 == 0) {
                    return 2;
                }
                else {
                    return 1;
                }
            }
        });

        recycler.setLayoutManager(manager);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_GAME, mGame);
        outState.putBoolean(STATE_FAVORITE, favorites);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewsFragmentInteractionListener) {
            mListener = (OnNewsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //News to show
    public void setGameNews(String game, boolean favorites) {
        mGame = game;
        this.favorites = favorites;

        if(currentSub != null) {
            currentSub.dispose();
        }

        Flowable<List<New>> newsList = viewModel.getAllnews();
        if(mGame != null) {
            //filter news by game
            newsList = newsList.map(news -> {
                ArrayList<New> filteredNews = new ArrayList<>();
                for (New aNew : news) {
                    if(aNew.getGame().equals(mGame) && (!favorites || aNew.isFavorite()) ) {

                        filteredNews.add(aNew);
                    }
                }
                return filteredNews;
            });
        }
        else if (favorites) {
             newsList = newsList.map(news -> {
                ArrayList<New> filteredNews = new ArrayList<>();
                for (New aNew : news) {
                    if(aNew.isFavorite()) {
                        filteredNews.add(aNew);
                    }
                }
                return filteredNews;
            });
        }


        currentSub = newsList.subscribe(players -> adapter.setDataSet(players));

    }


    //ADAPTER CALLBACKS

    @Override
    public void onFavoriteSelected(New aNew) {
        viewModel.toggleFavoriteNew(aNew);
    }

    @Override
    public void onNewSelected(New aNew) {
        //TODO: Show selected New (Dialog?)
        Toast.makeText(this.getContext(), "Selected New: "+aNew.getTitle(), Toast.LENGTH_SHORT).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNewsFragmentInteractionListener {

    }
}
