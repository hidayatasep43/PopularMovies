package com.hidayatasep.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hidayatasep.popularmovies.R;
import com.hidayatasep.popularmovies.model.TrillerFilm;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hidayatasep43 on 7/26/2017.
 */

public class TrillerAdapter extends RecyclerView.Adapter<TrillerAdapter.TrillerViewHolder> {

    private List<TrillerFilm> mTrillerFilmList;
    private Context mContext;
    final private OnTrillerItemClickListener mListener;

    public TrillerAdapter(List<TrillerFilm> trillerFilmList, Context context, OnTrillerItemClickListener listener) {
        mTrillerFilmList = trillerFilmList;
        mContext = context;
        mListener = listener;

    }

    @Override
    public TrillerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_triller,parent, false);
        return new TrillerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrillerViewHolder holder, int position) {
        TrillerFilm trillerFilm = mTrillerFilmList.get(position);
        Picasso.with(mContext)
                .load(trillerFilm.getImageUrl())
                //.placeholder(R.drawable.user_placeholder)
                //.error(R.drawable.user_placeholder_error)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mTrillerFilmList.size();
    }

    public class TrillerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mImageView;

        public TrillerViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onTrillerItemClicked(mTrillerFilmList.get(position).getYoutubeUrl());
        }
    }

    public interface OnTrillerItemClickListener{
        public void onTrillerItemClicked(String youtubeUrl);
    }

}
