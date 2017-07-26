package com.hidayatasep.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hidayatasep.popularmovies.R;

import java.util.List;

/**
 * Created by hidayatasep43 on 7/26/2017.
 */

public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.UserReviewHolder>{

    private List<com.hidayatasep.popularmovies.model.UserReview> mUserReviewList;
    private Context mContext;

    public UserReviewAdapter(List<com.hidayatasep.popularmovies.model.UserReview> userReviewList, Context context) {
        mUserReviewList = userReviewList;
        mContext = context;
    }

    @Override
    public UserReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_review, parent, false);
        return new UserReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserReviewHolder holder, int position) {
        com.hidayatasep.popularmovies.model.UserReview userReview = mUserReviewList.get(position);
        holder.namaUserReview.setText(userReview.getName());
        holder.mReview.setText(userReview.getReview());
    }

    @Override
    public int getItemCount() {
        return mUserReviewList.size();
    }

    public class UserReviewHolder extends RecyclerView.ViewHolder{

        public TextView namaUserReview;
        public TextView mReview;

        public UserReviewHolder(View itemView) {
            super(itemView);
            namaUserReview = (TextView) itemView.findViewById(R.id.tv_name);
            mReview = (TextView) itemView.findViewById(R.id.tv_review);
        }
    }


}
