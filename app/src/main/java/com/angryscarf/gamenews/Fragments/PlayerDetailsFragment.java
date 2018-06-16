package com.angryscarf.gamenews.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.Model.GameNewsViewModel;
import com.angryscarf.gamenews.R;
import com.angryscarf.gamenews.Util.Util;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPlayerDetFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLAYER = "player_arg";



    private OnPlayerDetFragmentInteractionListener mListener;

    private GameNewsViewModel viewModel;

    private Player selectedPlayer;

    private ImageView avatar;
    private TextView name, game, bio;
    private FrameLayout gameColor;


    public PlayerDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewDetailsFragment.
     */

    public static PlayerDetailsFragment newInstance(@Nullable Player player) {
        PlayerDetailsFragment fragment = new PlayerDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYER, player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedPlayer = (Player) getArguments().getSerializable(ARG_PLAYER);
        }

        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_details, container, false);

        avatar = v.findViewById(R.id.player_details_image_avatar);

        name = v.findViewById(R.id.player_details_text_name);
        game = v.findViewById(R.id.player_details_text_game);
        bio = v.findViewById(R.id.player_details_text_bio);
        gameColor = v.findViewById(R.id.player_details_frame_game);

        reload();

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayerDetFragmentInteractionListener) {
            mListener = (OnPlayerDetFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayerDetFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void reload() {

        if (selectedPlayer == null) return;
        Log.d("PLAYER_DETAILS", "DEBUG: aPlayer == null -> "+ (selectedPlayer == null));

        Picasso.get()
                .load(selectedPlayer.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .fit()
                .into(avatar);

        name.setText(Util.filterEmpty(selectedPlayer.getName(), getString(R.string.default_empty_player_name)));
        game.setText(Util.getGameName(selectedPlayer.getGame()));
        bio.setText(Util.filterEmpty(selectedPlayer.getBio(), getString(R.string.default_empty_player_bio)));

        gameColor.setBackgroundColor(getResources().getColor(Util.getGameColorID(selectedPlayer.getGame())));

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
    public interface OnPlayerDetFragmentInteractionListener {
    }
}
