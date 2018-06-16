package com.angryscarf.gamenews.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angryscarf.gamenews.Fragments.Adapters.GamePagerAdapter;
import com.angryscarf.gamenews.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGameFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GAME = "arg_game";
    private static final String ARG_FAVORITE = "arg_favorites";

    private static final String STATE_GAME = "state_game";
    private static final String STATE_FAVORITE = "state_favorites";

    private String mGame;
    private boolean favorites;

    private OnGameFragmentInteractionListener mListener;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GamePagerAdapter pagerAdapter;
    private NewsFragment newsFragment;
    private PlayersFragment playersFragment;


    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param game game to show.
     * @param favorite filter favorites.
     * @return A new instance of fragment GameFragment.
     */
    public static GameFragment newInstance(String game, boolean favorite) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME, game);
        args.putBoolean(ARG_FAVORITE, favorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame= getArguments().getString(ARG_GAME);
            favorites = getArguments().getBoolean(ARG_FAVORITE);
        }

        pagerAdapter = new GamePagerAdapter(getChildFragmentManager());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game, container, false);

        if(savedInstanceState == null) {
            newsFragment = NewsFragment.newInstance(mGame, favorites);
            playersFragment = PlayersFragment.newInstance(mGame);
        }
        else {
            newsFragment = (NewsFragment) getChildFragmentManager().getFragments().get(0);
            playersFragment = (PlayersFragment) getChildFragmentManager().getFragments().get(1);

            mGame = savedInstanceState.getString(STATE_GAME);
            favorites = savedInstanceState.getBoolean(STATE_FAVORITE);
        }

        pagerAdapter.addFragment(newsFragment);
        pagerAdapter.addFragment(playersFragment);



        viewPager = v.findViewById(R.id.game_view_pager);
        tabLayout = v.findViewById(R.id.game_tab_layout);


        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_games);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_player);


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameFragmentInteractionListener) {
            mListener = (OnGameFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGameFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_GAME,mGame);
        outState.putBoolean(STATE_FAVORITE, favorites);
    }

    public void filterByGame(String game) {
        this.mGame = game;
        newsFragment.setGameNews(game, favorites);
        playersFragment.setGamePlayers(game);
    }

    public void filterFavorites(boolean fav) {
        this.favorites = fav;
        newsFragment.setGameNews(mGame, fav);
    }

    public boolean isFavorites() {
        return favorites;
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
    public interface OnGameFragmentInteractionListener {
    }
}
