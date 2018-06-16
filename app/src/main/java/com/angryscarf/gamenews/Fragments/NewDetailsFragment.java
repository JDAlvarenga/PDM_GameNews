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

import com.angryscarf.gamenews.Data.GameNewsRepository;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.GameNewsViewModel;
import com.angryscarf.gamenews.R;
import com.angryscarf.gamenews.Util.Util;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNewDetFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NEW = "new_arg";



    private OnNewDetFragmentInteractionListener mListener;

    private GameNewsViewModel viewModel;

    private New selectedNew;

    private ImageView cover, favorite;
    private TextView title, description, body;
    private FrameLayout game;


    public NewDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewDetailsFragment.
     */

    public static NewDetailsFragment newInstance(@Nullable New aNew) {
        NewDetailsFragment fragment = new NewDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEW, aNew);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedNew = (New) getArguments().getSerializable(ARG_NEW);
        }

        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_details, container, false);

        cover = v.findViewById(R.id.new_details_image_cover);
        favorite = v.findViewById(R.id.new_details_image_favorite);
        title = v.findViewById(R.id.new_details_text_title);
        description = v.findViewById(R.id.new_details_text_description);
        body = v.findViewById(R.id.new_details_text_body);
        game = v.findViewById(R.id.new_details_frame_game);



            reload();

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewDetFragmentInteractionListener) {
            mListener = (OnNewDetFragmentInteractionListener) context;
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

        if (selectedNew == null) return;

        Picasso.get()
                .load(selectedNew.getCover())
                .placeholder(R.drawable.cover_default_no_game)
                .fit()
                .into(cover);

        favorite.setImageResource(selectedNew.isFavorite()? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        //TODO: move to resources
        title.setText(Util.filterEmpty(selectedNew.getTitle(), "No title"));
        description.setText(Util.filterEmpty(selectedNew.getDescription(), ""));
        body.setText(Util.filterEmpty(selectedNew.getBody(), ""));

        favorite.setOnClickListener(view -> {

            viewModel.toggleFavoriteNew(selectedNew);
            favorite.setImageResource(selectedNew.isFavorite()? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        });

        game.setBackgroundColor(getResources().getColor(Util.getGameColorID(selectedNew.getGame())));
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
    public interface OnNewDetFragmentInteractionListener {
    }

}
