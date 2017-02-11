package com.sunnyface.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sunnyface.popularmovies.libs.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smac on 14/01/2017.
 */

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



    private long id;
    private String title;
    private String overview;
    private String release_date;
    private String poster_path;
    private String backdrop_path;
    private double vote_average;
    private long vote_count;

    public Movie(long id, String title, String overview, String release_date, String poster_path, String backdrop_path, double vote_average, long vote_count) {
        this.setId(id);
        this.setTitle(title);
        this.setOverview(overview);
        this.setRelease_date(release_date);
        this.setPoster_path(poster_path);
        this.setBackdrop_path(backdrop_path);
        this.setVote_average(vote_average);
        this.setVote_count(vote_count);
    }

    private Movie(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.poster_path = in.readString();
        this.backdrop_path = in.readString();
        this.vote_average = in.readDouble();
        this.vote_count = in.readLong();
    }

    public static Movie deserialize(JSONObject movieJsonObject) throws JSONException {

        Long id = movieJsonObject.getLong(Constants.MOVIE_KEY_ID);
        String title = movieJsonObject.getString(Constants.MOVIE_KEY_TITLE);
        String overview = movieJsonObject.getString(Constants.MOVIE_KEY_OVERVIEW);
        String release_date = movieJsonObject.getString(Constants.MOVIE_KEY_RELEASE_DATE);
        String poster_path = movieJsonObject.getString(Constants.MOVIE_KEY_POSTER_PATH);
        double vote_average = movieJsonObject.getDouble(Constants.MOVIE_KEY_VOTE_AVERAGE);
        String backdrop_path = movieJsonObject.getString(Constants.MOVIE_KEY_BACKDROP_PATH);
        long vote_count = movieJsonObject.getLong(Constants.MOVIE_KEY_VOTE_COUNT);

        Movie movie = new Movie(id, title, overview, release_date, poster_path, backdrop_path, vote_average, vote_count);
        return movie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeString(this.poster_path);
        dest.writeString(this.backdrop_path);
        dest.writeDouble(this.vote_average);
        dest.writeLong(this.vote_count);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public long getVote_count() {
        return vote_count;
    }

    public void setVote_count(long vote_count) {
        this.vote_count = vote_count;
    }

    public Uri getImageUrl(int size, String type) {

        String image_path = null;
        if (type.equals("cover")) {
            image_path = getPoster_path();
        } else if (type.equals("back")) {
            image_path = getBackdrop_path();
        }

        String widthPath;

        if (size <= 92)
            widthPath = "w92";
        else if (size <= 154)
            widthPath = "w154";
        else if (size <= 185)
            widthPath = "w185";
        else if (size <= 342)
            widthPath = "w342";
        else if (size <= 500)
            widthPath = "w500";
        else
            widthPath = "w780";

        Uri poster_uri = Uri.parse(Constants.IMAGES_BASE_URL)
                .buildUpon()
                .appendPath(widthPath)
                .appendEncodedPath(image_path)
                .build();
        Log.i("Poster_uri", poster_uri.toString());
        return poster_uri;
    }
}

