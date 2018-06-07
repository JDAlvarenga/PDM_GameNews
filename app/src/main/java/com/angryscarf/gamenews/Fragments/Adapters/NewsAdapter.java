package com.angryscarf.gamenews.Fragments.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime on 6/6/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private List<New> mDataSet;

    public NewsAdapter(List<New> mDataSet) {
        this.mDataSet = mDataSet != null? mDataSet: new ArrayList<>();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, description;

        public NewsViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_image_view);
            title = itemView.findViewById(R.id.item_text_title);
            description = itemView.findViewById(R.id.item_text_description);
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_new, parent, false);

        NewsViewHolder holder = new NewsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {

        New n = mDataSet.get(position);
        int imageplaceholder;
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

        }
        Picasso.get()
                .load(mDataSet.get(position).getCover())
                .placeholder(imageplaceholder)
                .fit()
                .into(holder.image);
//      TODO: Move text to resources
        holder.title.setText(filterEmpty(n.getTitle(), "No Title"));
        holder.description.setText(filterEmpty(n.getDescription(), "No Description"));
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



    //Helper
    private String filterEmpty(String text, String def) {
        return text != null? text: def;
    }
}
