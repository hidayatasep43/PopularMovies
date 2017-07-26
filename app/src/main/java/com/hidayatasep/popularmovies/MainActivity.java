package com.hidayatasep.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hidayatasep.popularmovies.adapter.MovieAdapter;
import com.hidayatasep.popularmovies.helper.CustomItemOffset;
import com.hidayatasep.popularmovies.helper.Util;
import com.hidayatasep.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnRecyclerViewItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<Movie> mMovieList;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView tvNoData;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUpSharedreference();

        mMovieList = new ArrayList<Movie>();

        if(savedInstanceState != null && savedInstanceState.containsKey("movie")){
            mMovieList = savedInstanceState.getParcelableArrayList("movie");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        tvNoData = (TextView) findViewById(R.id.tv_empty);

        mAdapter = new MovieAdapter(mMovieList, MainActivity.this, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new CustomItemOffset(MainActivity.this, R.dimen.margin_item));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if(mMovieList.isEmpty()){
            downloadMovie();
        }
    }

    public void setUpSharedreference(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, MyPreferenceActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_refresh){
            downloadMovie();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMovieList != null){
            outState.putParcelableArrayList("movie", mMovieList);
        }
    }

    //function for download movie
    public void downloadMovie(){
        String url = Util.getUrl(mSharedPreferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_default)));
        if(Util.isNetworkConnected(MainActivity.this)){
            new DownloadMovie().execute(url);
        }else {
            showNoConnectionView();
        }

    }

    @Override
    public void onRecyclerViewItemClicked(int position) {
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra("movie", mMovieList.get(position));
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_order_key))){
            downloadMovie();
        }
    }

    private class DownloadMovie extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressbar();
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
            Log.d(TAG, s);
            mProgressBar.setVisibility(View.GONE);
            if(s != null){
                try {
                    JSONObject object = new JSONObject(s);
                    JSONArray results = object.getJSONArray("results");
                    showRecyclerView();
                    mMovieList.clear();
                    mAdapter.notifyDataSetChanged();
                    for(int i=0; i<results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        mMovieList.add(movie);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG,e.toString());
                    showNoDataView();
                }
            }else {
                showNoDataView();
            }
        }
    }

    private void showProgressbar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
    }

    private void showNoDataView() {
        Toast.makeText(MainActivity.this, "Oops an error occurred", Toast.LENGTH_SHORT).show();
        mRecyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
    }
    private void showNoConnectionView() {
        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        mRecyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
    }


    private void showRecyclerView() {
        if(mRecyclerView.getVisibility() == View.GONE){
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if(tvNoData.getVisibility() == View.VISIBLE){
            tvNoData.setVisibility(View.GONE);
        }
    }
}
