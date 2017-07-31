package com.hidayatasep.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hidayatasep.popularmovies.helper.JsonHelper;

import org.json.JSONObject;

/**
 * Created by hidayatasep43 on 7/26/2017.
 */

public class TrillerFilm implements Parcelable{

    private String imageUrl;
    private String youtubeUrl;

    public TrillerFilm(String imageUrl, String youtubeUrl) {
        this.imageUrl = imageUrl;
        this.youtubeUrl = youtubeUrl;
    }

    public TrillerFilm(JSONObject object){
        String keyYoutube = JsonHelper.getStringJson(object,"key");
        imageUrl = "http://img.youtube.com/vi/"+ keyYoutube  +"/0.jpg";
        youtubeUrl = "https://www.youtube.com/watch?v=" + keyYoutube;
    }

    public TrillerFilm(Parcel in){
        imageUrl = in.readString();
        youtubeUrl = in.readString();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageUrl);
        parcel.writeString(youtubeUrl);
    }

    public static final Creator<TrillerFilm> CREATOR = new Creator<TrillerFilm>() {
        @Override
        public TrillerFilm createFromParcel(Parcel in) {
            return new TrillerFilm(in);
        }

        @Override
        public TrillerFilm[] newArray(int size) {
            return new TrillerFilm[size];
        }
    };
}
