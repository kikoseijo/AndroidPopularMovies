package com.sunnyface.popularmovies.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class Trailer {

    private String key;
    private String name;
    private String site;
    private String size;
    private String type;

    public Trailer(JSONObject trailer) throws JSONException {
        String id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.size = trailer.getString("size");
        this.type = trailer.getString("type");
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }


    public String getType() {
        return type;
    }

    public String getUrl() {
        return "https://www.youtube.com/watch?v=" + this.getKey();
    }
}
