package com.sunnyface.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunnyface.popularmovies.databinding.ActivityDetailBinding;
import com.sunnyface.popularmovies.databinding.ActivityMainBinding;
import com.sunnyface.popularmovies.libs.Constants;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Root Activity.
 * Loads
 */
public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private boolean isTableLayout;
    private FragmentManager fragmentManager;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = binding.actionBar.toolbar;
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();


        if (binding.movieDetailContainer != null) {
            isTableLayout = true;
            if (savedInstanceState == null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.movie_detail_container, new MovieDetailFragment(), Constants.MOVIE_DETAIL_FRAGMENT_TAG);
                fragmentTransaction.commit();
            }
        } else {
            isTableLayout = false;
            getSupportActionBar().setElevation(0.5f);
        }
    }


    @Override
    public void onItemSelected(Movie movie, View view) {
        if (isTableLayout) {
            Bundle args = new Bundle();
            args.putBoolean("isTabletLayout", true);
            args.putParcelable("movie", movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.movie_detail_container, fragment, Constants.MOVIE_DETAIL_FRAGMENT_TAG);   //Replace its key.
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", movie);
            String movieID = "" + movie.getId();
            Log.i("movieID: ", movieID);
            // Doing some view transitions with style,
            // But, we must check if we're running on Android 5.0 or higher to work.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //KK Bind this to remove findViewByID??????
                ImageView thumbnailImageView = (ImageView) view.findViewById(R.id.thumbnail);
                TextView titleTextView = (TextView) view.findViewById(R.id.title);
                Pair<View, String> transition_a = Pair.create((View) thumbnailImageView, "movie_cover");
                Pair<View, String> transition_b = Pair.create((View) titleTextView, "movie_title");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, transition_a, transition_b);
                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
            }
        }
    }

}
