package com.angryscarf.gamenews.Fragments.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.Data.Player;
import com.angryscarf.gamenews.R;
import com.angryscarf.gamenews.Util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime on 6/6/2018.
 */

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>{

    private onPlayersAdapterInteractionListener mListener;
    private List<Player> mDataSet;
    private Context context;

    public PlayersAdapter(Context context, onPlayersAdapterInteractionListener mListener, List<Player> mDataSet) {
        this.context = context;
        this.mListener = mListener;
        this.mDataSet = mDataSet != null? mDataSet: new ArrayList<>();
    }

    public static class PlayersViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ImageView avatar;
        public TextView name, game;
        public FrameLayout gameColor;

        public PlayersViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.player_item_image_avatar);
            name = itemView.findViewById(R.id.player_item_text_name);
            game = itemView.findViewById(R.id.player_item_text_game);
            gameColor = itemView.findViewById(R.id.player_item_frame_game);
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

        holder.name.setText(Util.filterEmpty(player.getName(), context.getString(R.string.default_empty_player_name)));
        holder.game.setText(Util.getGameName(player.getGame()));

        Picasso.get()
                .load(player.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .fit()
                .into(holder.avatar);

        holder.itemView.setOnClickListener(view -> {
            mListener.onPlayerSelected(player);
        });
        holder.gameColor.setBackgroundColor(context.getResources().getColor(Util.getGameColorID(player.getGame())));
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


}
