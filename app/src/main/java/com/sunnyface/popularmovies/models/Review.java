package com.sunnyface.popularmovies.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class Review {

    private String author;
    private String content;
    private String url;

    public Review(JSONObject review) throws JSONException {
        String id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");
        this.url = review.getString("url");
    }

    public String getAuthor() {
        return author;
    }
    public String getUrl() { return url; }

    public String getContent() {
        return content;
    }

}
