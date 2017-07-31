package com.hidayatasep.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hidayatasep.popularmovies.adapter.TrillerAdapter;
import com.hidayatasep.popularmovies.adapter.UserReviewAdapter;
import com.hidayatasep.popularmovies.database.MovieProvider;
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

import static com.hidayatasep.popularmovies.database.MovieContract.BACKDROP_URL;
import static com.hidayatasep.popularmovies.database.MovieContract.CONTENT_URI;
import static com.hidayatasep.popularmovies.database.MovieContract.IMAGE_URL;
import static com.hidayatasep.popularmovies.database.MovieContract.RELEASE_DATE;
import static com.hidayatasep.popularmovies.database.MovieContract.SINOPSIS;
import static com.hidayatasep.popularmovies.database.MovieContract.TITLE;
import static com.hidayatasep.popularmovies.database.MovieContract.USER_RATING;
import static com.hidayatasep.popularmovies.database.MovieContract._ID;

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
    boolean isFinishDownloadTriller = false;
    boolean isFinishDownloadUserReview = false;


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

        //fetching data from percable movie object
        if(savedInstanceState != null && savedInstanceState.containsKey("movie")){
            mMovie = savedInstanceState.getParcelable("movie");
        }else{
            mMovie = getIntent().getParcelableExtra("movie");
        }

        //fetching data trailer
        if(savedInstanceState != null && savedInstanceState.containsKey("isDownloadTrailer")){
            isFinishDownloadTriller = savedInstanceState.getBoolean("isDownloadTrailer");
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("trailer")){
            mTrillerFilmsList = savedInstanceState.getParcelableArrayList("trailer");
        }else{
            mTrillerFilmsList = new ArrayList<TrillerFilm>();
            if(!isFinishDownloadTriller){
                new DownloadTriller().execute(Util.getUrlTriller(mMovie.getId()));
            }
        }

        //fetching data user review
        if(savedInstanceState != null && savedInstanceState.containsKey("isDownloadUserReview")){
            isFinishDownloadUserReview = savedInstanceState.getBoolean("isDownloadUserReview");
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("userreview")){
            mUserReviewsList = savedInstanceState.getParcelableArrayList("userreview");
        }else{
            mUserReviewsList = new ArrayList<UserReview>();
            if(!isFinishDownloadUserReview){
                new DownloadUserRating().execute(Util.getUrlUserReview(mMovie.getId()));
            }
        }


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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mUserReviewsList != null){
            outState.putParcelableArrayList("userreview", mUserReviewsList);
        }
        if(mTrillerFilmsList != null){
            outState.putParcelableArrayList("trailer", mTrillerFilmsList);
        }
        if(mMovie != null){
            outState.putParcelable("movie",mMovie);
        }

        outState.putBoolean("isDownloadTrailer", isFinishDownloadTriller);
        outState.putBoolean("isDownloadUserReview", isFinishDownloadUserReview);

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
        values.put(_ID, mMovie.getId());
        values.put(TITLE, mMovie.getTitle());
        values.put(IMAGE_URL, mMovie.getImage());
        values.put(SINOPSIS, mMovie.getSinopsis());
        values.put(USER_RATING, mMovie.getUserRating());
        values.put(RELEASE_DATE, mMovie.getReleaseDate());
        values.put(BACKDROP_URL, mMovie.getImageBackdrop());

        Uri uri = getContentResolver().insert(
                CONTENT_URI, values);
        Log.d(TAG, uri.toString());
        Toast.makeText(this, "Add to favorite movie", Toast.LENGTH_SHORT).show();
    }

    private boolean checkMovie(){
        String[] projection = {
            TITLE
        };
        Uri uri = Uri.parse(CONTENT_URI + "/" + mMovie.getId());
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
        Uri uri = Uri.parse(CONTENT_URI + "/" + mMovie.getId());
        getContentResolver().delete(uri,null,null);
        Toast.makeText(this, "Delete from favorite movie", Toast.LENGTH_SHORT).show();
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
                        isFinishDownloadTriller = true;
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
                        isFinishDownloadUserReview = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }
    }

}
