package com.sunnyface.popularmovies;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunnyface.popularmovies.adapters.PagerAdapter;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.databinding.DetailFragmentBinding;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Created by Kiko Seijo on 12/02/2017.
 * by The Sunnyface.com.
 */

public class DetailFragment extends Fragment {

    private Movie movie;
    private Boolean isFavorite;
    private Toast mToast;
    private DetailFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment, container, false);

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Trailers"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        Bundle arguments = getArguments();
        movie = arguments.getParcelable("movie");
        isFavorite = Utils.isMovieOnDatabase(getContext(), (int) movie.getId());

        //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        if (binding.collapsingToolbarLayout != null) {
            //kk contrast on the background and the title color text.
            // Picasso cant paint cause no textview.
            Typeface customTitleFont = Typeface.createFromAsset(getContext().getAssets(),"fonts/Lato-Bold.ttf");
            binding.collapsingToolbarLayout.setExpandedTitleTypeface(customTitleFont);
            binding.collapsingToolbarLayout.setTitle(movie.getTitle());
            binding.collapsingToolbarLayout.setContentDescription(movie.getTitle());
            Utils.imageViewObserver(binding.poster, getContext(), "back", movie);

            binding.favouriteButton.setChecked(isFavorite);
            binding.favouriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    changeFavoriteStatus();
                }
            });

        }

        final ViewPager viewPager = binding.pager;
        final PagerAdapter adapter = new PagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), arguments);
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

        return binding.getRoot();
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
                getContext().getContentResolver().delete(
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

        Utils.addMovieToDatabase(getContext(), movie);

        showToast(getString(R.string.added_to_favorites));
        isFavorite = !isFavorite;
        binding.favouriteButton.setChecked(isFavorite);

    }

    private void showToast(String msg){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
