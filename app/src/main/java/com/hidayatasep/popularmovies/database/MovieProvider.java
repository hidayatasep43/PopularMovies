package com.hidayatasep.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import static com.hidayatasep.popularmovies.database.DatabaseHelper.MOVIE_TABLE_NAME;
import static com.hidayatasep.popularmovies.database.MovieContract.BACKDROP_URL;
import static com.hidayatasep.popularmovies.database.MovieContract.CONTENT_URI;
import static com.hidayatasep.popularmovies.database.MovieContract.IMAGE_URL;
import static com.hidayatasep.popularmovies.database.MovieContract.PROVIDER_NAME;
import static com.hidayatasep.popularmovies.database.MovieContract.RELEASE_DATE;
import static com.hidayatasep.popularmovies.database.MovieContract.SINOPSIS;
import static com.hidayatasep.popularmovies.database.MovieContract.TITLE;
import static com.hidayatasep.popularmovies.database.MovieContract.USER_RATING;
import static com.hidayatasep.popularmovies.database.MovieContract._ID;

/**
 * Created by hidayatasep43 on 7/28/2017.
 */

public class MovieProvider extends ContentProvider {

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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MOVIE_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case MOVIE:
                queryBuilder.setProjectionMap(MOVIE_PROJECTION_MAP);
                break;
            case MOVIE_ID:
                queryBuilder.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if(sortOrder == null || sortOrder.equals("")){
            /**
             * By default sort on student names
             */
            sortOrder = TITLE;
        }

        Cursor c = queryBuilder.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case MOVIE:
                return "vnd.android.cursor.dir/vnd.com.hidayatasep.popularmovies.movieprovider.movie";
            /**
             * Get a particular student
             */
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.com.hidayatasep.popularmovies.movieprovider.movie";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //add a new movie record
        long rowID = db.insert(MOVIE_TABLE_NAME,"",contentValues);

        /**
         * If record is added successfully
         */
        if(rowID > 0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case MOVIE:
                count = db.delete(MOVIE_TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( MOVIE_TABLE_NAME, _ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                count = db.update(MOVIE_TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case MOVIE_ID:
                count = db.update(MOVIE_TABLE_NAME, contentValues,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
