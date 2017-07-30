package com.hidayatasep.popularmovies.contentprovider;

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

import java.util.HashMap;

/**
 * Created by hidayatasep43 on 7/28/2017.
 */

public class MovieProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.hidayatasep.popularmovies.movieprovider";
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
