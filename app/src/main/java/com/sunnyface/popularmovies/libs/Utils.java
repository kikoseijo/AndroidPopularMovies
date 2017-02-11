package com.sunnyface.popularmovies.libs;

/**
 * Created by smac on 14/01/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.BuildConfig;
import com.sunnyface.popularmovies.models.Movie;

public class Utils {


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String buildMoviesUrl(String sort_type, int page_num) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort_type)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_KEY)
                .appendQueryParameter("page", Integer.toString(page_num));

        String imgUrl = builder.build().toString();

        return imgUrl;
    }

    public static void imageViewObserver(final ImageView imageView, final Context context, final String type, final Movie movie) {
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = imageView.getWidth();
                //Log.d("TEST", "Width = " + width + " Height = " + imageView.getHeight());

                Picasso
                        .with(context)
                        .load(movie.getImageUrl(width, type))
                        .into(imageView);

                ViewTreeObserver obs = imageView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }


}
