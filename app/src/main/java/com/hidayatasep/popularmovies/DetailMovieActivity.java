package com.hidayatasep.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.hidayatasep.popularmovies.helper.Constant;
import com.hidayatasep.popularmovies.helper.Util;
import com.hidayatasep.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailMovieActivity extends AppCompatActivity {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    Movie mMovie;
    ImageView mImageBackdrop, mImagePoster;
    TextView mTextViewTitle, mTextViewReleaseDate, mTextViewSinopsis, mTextViewUserRating;
    RecyclerView mRecyclerTriller, mRecyclerUserReview;


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
        mRecyclerTriller = (RecyclerView) findViewById(R.id.recycler_view_triller);
        mRecyclerUserReview = (RecyclerView) findViewById(R.id.recycler_view_user_review);

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

    private class Download extends AsyncTask<String, Void, String>{

        private String TYPE_TRILLER = "triller";
        private String TYPE_USER_RATING = "user_rating";
        private String type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            if(strings != null){
                String urlString = strings[0];
                //get data from api
                try {
                    URL url = new URL(urlString);
                    result = Util.downloadUrl(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

}
