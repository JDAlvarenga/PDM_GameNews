package com.angryscarf.gamenews.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.angryscarf.gamenews.Fragments.Adapters.PlayersAdapter;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.Model.GameNewsViewModel;
import com.angryscarf.gamenews.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPlayersFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayersFragment extends Fragment implements PlayersAdapter.onPlayersAdapterInteractionListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GAME = "arg_game";

    //State variable keys
    private static final String STATE_GAME = "state_game";

    private String mGame;
    private OnPlayersFragmentInteractionListener mListener;
    private RecyclerView recycler;
    private PlayersAdapter adapter;
    private GameNewsViewModel viewModel;

    private Disposable currentSub;

    public PlayersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param game Game to show.
     * @return A new instance of fragment PlayersFragment.
     */
    public static PlayersFragment newInstance(String game) {
        PlayersFragment fragment = new PlayersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame = getArguments().getString(ARG_GAME);
        }

        if (savedInstanceState != null) {
            mGame = savedInstanceState.getString(STATE_GAME);
        }

        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);
        adapter = new PlayersAdapter(this, null);

        setGamePlayers(mGame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_players, container, false);

        recycler = v.findViewById(R.id.players_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_GAME, mGame);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayersFragmentInteractionListener) {
            mListener = (OnPlayersFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayersFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Players to show
    public void setGamePlayers(String game) {
        mGame = game;

        if(currentSub != null) {
            currentSub.dispose();
        }

        Flowable<List<Player>> playersList = viewModel.getAllplayers();
        if(mGame != null) {
            //filter players by game
            playersList = playersList.map(players -> {
                ArrayList filteredPlayers = new ArrayList();
                for (Player player : players) {
                    if(player.getGame().equals(mGame)) {
                        filteredPlayers.add(player);
                    }
                }
                return filteredPlayers;
            });
        }

        currentSub = playersList.subscribe(players -> adapter.setDataSet(players));
    }


    //ADAPTER CALLBACKS


    @Override
    public void onPlayerSelected(Player player) {
        //TODO: Show selected player info (Dialog?)
        Toast.makeText(this.getContext(), "Selected Player: "+player.getName(), Toast.LENGTH_SHORT).show();
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
    public interface OnPlayersFragmentInteractionListener {

    }
}
