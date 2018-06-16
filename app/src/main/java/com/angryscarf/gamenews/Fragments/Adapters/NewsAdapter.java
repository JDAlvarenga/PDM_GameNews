package com.angryscarf.gamenews.Fragments.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.R;
import com.angryscarf.gamenews.Util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime on 6/6/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private int FAVORITE_ICON = R.drawable.ic_favorite;
    private int FAVORITE_BORDER_ICON = R.drawable.ic_favorite_border;


    private Context context;
    private onNewsAdapterInteractionListener mListener;
    private List<New> mDataSet;

    public NewsAdapter(Context context, onNewsAdapterInteractionListener mListener, List<New> mDataSet) {
        this.context = context;
        this.mListener = mListener;
        this.mDataSet = mDataSet != null? mDataSet: new ArrayList<>();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView image, favorite;
        public TextView title, description;
        public FrameLayout game;

        public NewsViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.new_item_image_view);
            favorite = itemView.findViewById(R.id.new_item_image_favorite);
            title = itemView.findViewById(R.id.new_item_text_title);
            description = itemView.findViewById(R.id.new_item_text_description);
            game = itemView.findViewById(R.id.new_item_frame_game);
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_recycler_item, parent, false);

        NewsViewHolder holder = new NewsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {

        New n = mDataSet.get(position);
        //Dynamic image loading by game (placeholder)
      /*  int imageplaceholder;
        switch (n.getGame()) {

            case "lol":
                imageplaceholder = R.drawable.cover_default_lol;
                break;

            case "csgo":
                imageplaceholder = R.drawable.cover_default_csgo;
                break;

            case "overwatch":
                imageplaceholder = R.drawable.cover_default_overwatch;
                break;

            default:
                imageplaceholder = R.drawable.cover_default_no_game;

        }*/
        Picasso.get()
                .load(mDataSet.get(position).getCover())
                .placeholder(R.drawable.cover_default_no_game)
                .fit()
                .into(holder.image);
//      TODO: Move text to resources
        holder.title.setText(Util.filterEmpty(n.getTitle(), "No Title"));
        holder.description.setText(Util.filterEmpty(n.getDescription(), "No Description"));

        holder.favorite.setImageResource(n.isFavorite()? FAVORITE_ICON: FAVORITE_BORDER_ICON);

        holder.itemView.setOnClickListener(view -> {
            mListener.onNewSelected(n);
        });
        holder.game.setBackgroundColor(context.getResources().getColor(Util.getGameColorID(n.getGame() )) );

        holder.favorite.setOnClickListener(view -> {
            Log.d("DEBUG", "Called listener");
            mListener.onFavoriteSelected(n);
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    //Interaction
    public void setDataSet(List<New> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }


    public interface onNewsAdapterInteractionListener {
        void onNewSelected(New aNew);
        void onFavoriteSelected(New aNew);
    }


}
