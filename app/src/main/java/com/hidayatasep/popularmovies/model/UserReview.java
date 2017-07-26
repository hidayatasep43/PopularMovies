package com.hidayatasep.popularmovies.model;

import com.hidayatasep.popularmovies.helper.JsonHelper;

import org.json.JSONObject;

/**
 * Created by hidayatasep43 on 7/26/2017.
 */

public class UserReview {

    private String name;
    private String review;

    public UserReview(String name, String review) {
        this.name = name;
        this.review = review;
    }

    public UserReview(JSONObject object) {
        name = JsonHelper.getStringJson(object,"author");
        review = JsonHelper.getStringJson(object,"content");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
