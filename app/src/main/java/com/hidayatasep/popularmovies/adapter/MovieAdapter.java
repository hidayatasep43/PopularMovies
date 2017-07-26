package com.hidayatasep.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hidayatasep.popularmovies.R;
import com.hidayatasep.popularmovies.helper.Constant;
import com.hidayatasep.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hidayatasep43 on 6/29/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder>{

    private List<Movie> mMovieList;
    private Context mContext;

    final private OnRecyclerViewItemClickListener mListener;

    public MovieAdapter(List<Movie> movieList, Context context, OnRecyclerViewItemClickListener listener) {
        mMovieList = movieList;
        mContext = context;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        Picasso.with(mContext)
                .load(Constant.IMAGE_BASE_URL + movie.getImage())
                .resize(200,300)
                //.placeholder(R.drawable.user_placeholder)
                //.error(R.drawable.user_placeholder_error)
                .into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView movieImage;
        public TextView movieTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.image_movie);
            movieTitle = (TextView) itemView.findViewById(R.id.title_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onRecyclerViewItemClicked(position);
        }
    }

    public interface OnRecyclerViewItemClickListener{
        public void onRecyclerViewItemClicked(int position);
    }


}
