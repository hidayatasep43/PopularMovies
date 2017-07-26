package com.hidayatasep.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hidayatasep.popularmovies.helper.Constant;
import com.hidayatasep.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailMovieActivity extends AppCompatActivity {

    Movie mMovie;
    ImageView mImageBackdrop, mImagePoster;
    TextView mTextViewTitle, mTextViewReleaseDate, mTextViewSinopsis, mTextViewUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        mImagePoster = (ImageView) findViewById(R.id.image_movie);
        mImageBackdrop = (ImageView) findViewById(R.id.image_backdroph);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        mTextViewReleaseDate = (TextView) findViewById(R.id.tv_releases);
        mTextViewSinopsis = (TextView) findViewById(R.id.tv_sinopsis);
        mTextViewUserRating = (TextView) findViewById(R.id.tv_userRating);

        //fetching data from percable object
        mMovie = getIntent().getParcelableExtra("movie");

        Picasso.with(DetailMovieActivity.this)
                .load(Constant.IMAGE_BASE_URL + mMovie.getImage())
                //.placeholder(R.drawable.user_placeholder)
                //.error(R.drawable.user_placeholder_error)
                .into(mImagePoster);

        Picasso.with(DetailMovieActivity.this)
                .load(Constant.IMAGE_BACKDROPH_BASE_URL + mMovie.getImageBackdrop())
                //.placeholder(R.drawable.user_placeholder)
                //.error(R.drawable.user_placeholder_error)
                .into(mImageBackdrop);

        mTextViewTitle.setText(mMovie.getTitle());
        mTextViewReleaseDate.setText(mMovie.getReleaseDate());
        mTextViewSinopsis.setText(mMovie.getSinopsis());
        mTextViewUserRating.setText(mMovie.getUserRating() + "/10");

    }


}
