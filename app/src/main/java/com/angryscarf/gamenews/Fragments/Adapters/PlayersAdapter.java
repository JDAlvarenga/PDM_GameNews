package com.angryscarf.gamenews.Fragments.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime on 6/6/2018.
 */

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>{

    private onPlayersAdapterInteractionListener mListener;
    private List<Player> mDataSet;

    public PlayersAdapter(onPlayersAdapterInteractionListener mListener, List<Player> mDataSet) {
        this.mListener = mListener;
        this.mDataSet = mDataSet != null? mDataSet: new ArrayList<>();
    }

    public static class PlayersViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ImageView avatar;
        public TextView name, game;

        public PlayersViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.player_item_image_avatar);
            name = itemView.findViewById(R.id.player_item_text_name);
            game = itemView.findViewById(R.id.player_item_text_game);
        }
    }

    @Override
    public PlayersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_recycler_item, parent, false);

        PlayersViewHolder holder = new PlayersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlayersViewHolder holder, int position) {

        Player player = mDataSet.get(position);

        holder.name.setText(filterEmpty(player.getName(), "Unknown player"));
        holder.game.setText(filterEmpty(player.getGame(), "Unknown game"));

        Picasso.get()
                .load(player.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .fit()
                .into(holder.avatar);

        holder.itemView.setOnClickListener(view -> {
            mListener.onPlayerSelected(player);
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    //Interaction
    public void setDataSet(List<Player> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }


    public interface onPlayersAdapterInteractionListener {
        void onPlayerSelected(Player player);
    }


    //Helper
    private String filterEmpty(String text, String def) {
        return text != null? text: def;
    }
}
