package com.sunnyface.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sunnyface.popularmovies.adapters.PagerAdapter;
import com.sunnyface.popularmovies.databinding.ActivityDetailBinding;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class DetailActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
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

        //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        if (binding.collapsingToolbarLayout != null) {

            binding.collapsingToolbarLayout.setTitle(movie.getTitle());
            Utils.imageViewObserver(binding.poster, this, "back", movie);
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



}