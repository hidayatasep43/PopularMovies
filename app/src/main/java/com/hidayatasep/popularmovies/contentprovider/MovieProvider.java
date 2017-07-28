package com.hidayatasep.popularmovies.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by hidayatasep43 on 7/28/2017.
 */

public class MovieProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.hidayatasep.popularmovies.MovieProvider";
    public static final String URL_PROVIDER = "content://" + PROVIDER_NAME + "/movie";
    public static final Uri CONTENT_URI = Uri.parse(URL_PROVIDER);

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageurl";
    public static final String SINOPSIS = "_sinopsis";
    public static final String USER_RATING = "userrating";
    public static final String RELEASE_DATE = "releasedate";
    public static final String BACKDROP_URL = "backdropurl";

    private static HashMap<String, String> MOVIE_PROJECTION_MAP;

    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"movie",MOVIE);
        uriMatcher.addURI(PROVIDER_NAME,"movie/#",MOVIE_ID);
    }

    /*
    * Database spesific devlaration
    * */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Movie";
    static final String MOVIE_TABLE_NAME = "movie";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOVIE_TABLE_NAME + "(" +
                    _ID + "LONG PRIMARY KEY , " +
                    TITLE + "TEXT NOT NULL, " +
                    IMAGE_URL + "TEXT NOT NULL, " +
                    SINOPSIS+ "TEXT NOT NULL, " +
                    USER_RATING + "FLOAT NOT NULL, " +
                    RELEASE_DATE + "TEXT NOT NULL, " +
                    BACKDROP_URL + "TEXT NOT NULL); ";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MOVIE_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db != null) ;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
