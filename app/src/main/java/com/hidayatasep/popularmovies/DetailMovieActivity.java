package com.hidayatasep.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hidayatasep.popularmovies.adapter.MovieAdapter;
import com.hidayatasep.popularmovies.adapter.TrillerAdapter;
import com.hidayatasep.popularmovies.adapter.UserReviewAdapter;
import com.hidayatasep.popularmovies.contentprovider.MovieProvider;
import com.hidayatasep.popularmovies.helper.Constant;
import com.hidayatasep.popularmovies.helper.Util;
import com.hidayatasep.popularmovies.model.Movie;
import com.hidayatasep.popularmovies.model.TrillerFilm;
import com.hidayatasep.popularmovies.model.UserReview;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class DetailMovieActivity extends AppCompatActivity implements TrillerAdapter.OnTrillerItemClickListener, View.OnClickListener{

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    Movie mMovie;
    ImageView mImageBackdrop, mImagePoster;
    TextView mTextViewTitle, mTextViewReleaseDate, mTextViewSinopsis, mTextViewUserRating;
    RecyclerView mRecyclerTriller, mRecyclerUserReview;
    ImageButton mImageButtonBookmark;

    private ArrayList<TrillerFilm> mTrillerFilmsList;
    private ArrayList<UserReview> mUserReviewsList;

    TrillerAdapter mTrillerAdapter;
    UserReviewAdapter mUserReviewAdapter;

    boolean isBookmark = false;


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
        mImageButtonBookmark = (ImageButton) findViewById(R.id.btn_bookmark);

        //fetching data from percable object
        mMovie = getIntent().getParcelableExtra("movie");

        mTrillerFilmsList = new ArrayList<TrillerFilm>();
        mUserReviewsList = new ArrayList<UserReview>();

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


        setImageBookmark();

        //triller
        mTrillerAdapter = new TrillerAdapter(mTrillerFilmsList, DetailMovieActivity.this, DetailMovieActivity.this);
        RecyclerView.LayoutManager layoutManagerTriller = new LinearLayoutManager(DetailMovieActivity.this, LinearLayoutManager.HORIZONTAL,false);
        mRecyclerTriller.setLayoutManager(layoutManagerTriller);
        mRecyclerTriller.setItemAnimator(new DefaultItemAnimator());
        mRecyclerTriller.setAdapter(mTrillerAdapter);

        mUserReviewAdapter = new UserReviewAdapter(mUserReviewsList, DetailMovieActivity.this);
        RecyclerView.LayoutManager layoutManagerUserReview = new LinearLayoutManager(DetailMovieActivity.this);
        mRecyclerUserReview.setLayoutManager(layoutManagerUserReview);
        mRecyclerUserReview.setItemAnimator(new DefaultItemAnimator());
        mRecyclerUserReview.setAdapter(mUserReviewAdapter);

        mImageButtonBookmark.setOnClickListener(this);


        new DownloadTriller().execute(Util.getUrlTriller(mMovie.getId()));
        new DownloadUserRating().execute(Util.getUrlUserReview(mMovie.getId()));


    }

    private void setImageBookmark() {
        isBookmark = checkMovie();
        if(isBookmark){
            mImageButtonBookmark.setBackgroundResource(R.drawable.star);
        }else {
            mImageButtonBookmark.setBackgroundResource(R.drawable.star_white);
        }
    }

    @Override
    public void onTrillerItemClicked(String youtubeUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtubeUrl));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        isBookmark = checkMovie();
        if(isBookmark){
            deleteRecord();
        }else{
            insertMovie();
        }
        setImageBookmark();
    }

    private void insertMovie() {
        ContentValues values = new ContentValues();
        values.put(MovieProvider._ID, mMovie.getId());
        values.put(MovieProvider.TITLE, mMovie.getTitle());
        values.put(MovieProvider.IMAGE_URL, mMovie.getImage());
        values.put(MovieProvider.SINOPSIS, mMovie.getSinopsis());
        values.put(MovieProvider.USER_RATING, mMovie.getUserRating());
        values.put(MovieProvider.RELEASE_DATE, mMovie.getReleaseDate());
        values.put(MovieProvider.BACKDROP_URL, mMovie.getImageBackdrop());

        Uri uri = getContentResolver().insert(
                MovieProvider.CONTENT_URI, values);
        Log.d(TAG, uri.toString());
    }

    private boolean checkMovie(){
        String[] projection = {
            MovieProvider.TITLE
        };
        Uri uri = Uri.parse(MovieProvider.CONTENT_URI + "/" + mMovie.getId());
        Cursor cursor = getContentResolver().query(uri, projection,null,null,null);
        if(cursor == null){
            return false;
        }
        if(cursor.getCount() == 0){
            return false;
        }
        return true;
    }

    private void deleteRecord(){
        Uri uri = Uri.parse(MovieProvider.CONTENT_URI + "/" + mMovie.getId());
        getContentResolver().delete(uri,null,null);
    }

    private class DownloadTriller extends AsyncTask<String, Void, String>{

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
            Log.d(TAG,s);
            if (s != null){
                if(!s.isEmpty()){
                    try {
                        JSONObject object = new JSONObject(s);
                        JSONArray results = object.getJSONArray("results");

                        mTrillerFilmsList.clear();
                        mTrillerAdapter.notifyDataSetChanged();
                        for(int i=0; i<results.length(); i++){
                            TrillerFilm trillerFilm = new TrillerFilm(results.getJSONObject(i));
                            mTrillerFilmsList.add(trillerFilm);
                            mTrillerAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,e.toString());
                    }
                }
            }

        }
    }

    private class DownloadUserRating extends AsyncTask<String, Void, String>{

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
            Log.d(TAG,s);
            if (s != null) {
                if (!s.isEmpty()) {
                    try {
                        JSONObject object = new JSONObject(s);
                        JSONArray results = object.getJSONArray("results");

                        mUserReviewsList.clear();
                        mUserReviewAdapter.notifyDataSetChanged();
                        for (int i = 0; i < results.length(); i++) {
                            UserReview userReview = new UserReview(results.getJSONObject(i));
                            mUserReviewsList.add(userReview);
                            mUserReviewAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }
    }

}
