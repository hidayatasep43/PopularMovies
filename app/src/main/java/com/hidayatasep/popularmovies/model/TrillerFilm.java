package com.hidayatasep.popularmovies.model;

import com.hidayatasep.popularmovies.helper.JsonHelper;

import org.json.JSONObject;

/**
 * Created by hidayatasep43 on 7/26/2017.
 */

public class TrillerFilm {

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
}
