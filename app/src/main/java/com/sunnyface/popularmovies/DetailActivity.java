package com.sunnyface.popularmovies;

import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.adapters.PagerAdapter;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.databinding.ActivityDetailBinding;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private Movie movie;
    private Boolean isFavorite;
    private Toast mToast;
    private ActivityDetailBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Toolbar toolbar = binding.detailActionBar;
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Trailers"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        Bundle bundle = new Bundle();
        bundle.putBundle("movie", getIntent().getExtras());
        movie = getIntent().getExtras().getParcelable("movie");
        isFavorite = Utils.isMovieOnDatabase(this, (int) movie.getId());

        //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        if (binding.collapsingToolbarLayout != null) {
            //kk contrast on the background and the title color text.
            // Picasso cant paint cause no textview.
            //
            Typeface customTitleFont = Typeface.createFromAsset(getAssets(),"fonts/Lato-Bold.ttf");
            binding.collapsingToolbarLayout.setExpandedTitleTypeface(customTitleFont);
            binding.collapsingToolbarLayout.setTitle(movie.getTitle());
            binding.collapsingToolbarLayout.setContentDescription(movie.getTitle());
            Utils.imageViewObserver(binding.poster, this, "back", movie);

            binding.favouriteButton.setChecked(isFavorite);
            binding.favouriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    changeFavoriteStatus();
                }
            });

        }

        final ViewPager viewPager = binding.pager;
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bundle);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



/*
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBundle("movie", getIntent().getExtras());
            MovieDetailFragment detailActivityFragment = new MovieDetailFragment();
            detailActivityFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.movie_detail_container, detailActivityFragment);
            fragmentTransaction.commit();
        }
*/
    }

    private void changeFavoriteStatus() {
        if (isFavorite) {
            deleteMovieFromDatabase();
        } else {
            addMovieToDatabase();
        }
    }

    private  void deleteMovieFromDatabase(){
        Integer isDeleted =
                this.getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString((int) movie.getId())});

        if (isDeleted>0){
            showToast(getString(R.string.removed_from_favorites));
            isFavorite = !isFavorite;
            binding.favouriteButton.setChecked(isFavorite);
        } else {
            showToast(getString(R.string.problem_adding_to_favorites));
        }
    }

    private void addMovieToDatabase(){

        Utils.addMovieToDatabase(this,movie);

        showToast(getString(R.string.added_to_favorites));
        isFavorite = !isFavorite;
        binding.favouriteButton.setChecked(isFavorite);

    }

    private void showToast(String msg){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

}