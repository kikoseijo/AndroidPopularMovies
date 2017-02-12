package com.sunnyface.popularmovies.libs;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.BuildConfig;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.data.MovieDbHelper;
import com.sunnyface.popularmovies.extensions.GradientTransformation;
import com.sunnyface.popularmovies.models.Movie;

public class Utils {

    public static Boolean isMovieOnDatabase(Context context, int id) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { Integer.toString(id) },
                null, null, null
        );
        int numRows = cursor.getCount();
        cursor.close();
        mDb.close();
        return numRows > 0 ;
    }

    public static void openWebViewWithUrlString(Context context, String url){
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String buildMoviesUrl(String sort_type, int page_num){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort_type)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_KEY)
                .appendQueryParameter("page", Integer.toString(page_num));

        return builder.build().toString();
    }

    public static String buildStage2Url(long movieID, String path){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Long.toString(movieID))
                .appendPath(path)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_KEY);

        return builder.build().toString();
    }



    public static void imageViewObserver(final ImageView imageView, final Context context, final String type, final Movie movie){
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = imageView.getWidth();
                //Log.d("TEST", "Width = " + width + " Height = " + imageView.getHeight());
                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
                if (type.equals("back")){
                    Picasso
                            .with(context)
                            .load(movie.getImageUrl(width, type))
                            .transform(new GradientTransformation())
                            .error(transparentDrawable)
                            .into(imageView);
                } else {
                    Picasso
                            .with(context)
                            .load(movie.getImageUrl(width, type))
                            .error(transparentDrawable)
                            .into(imageView);
                }


                ViewTreeObserver obs = imageView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    public static void addMovieToDatabase(Context context, Movie movie){
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVote_count());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());

        Uri rUri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        Log.i("MovieDb Updated::->", "rUri: " + rUri);

    }

}

