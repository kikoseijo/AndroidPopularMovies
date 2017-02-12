package com.sunnyface.popularmovies.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sunnyface.popularmovies.MovieDetailFragment;
import com.sunnyface.popularmovies.ReviewsDetailFragment;
import com.sunnyface.popularmovies.TrailersDetailFragment;

/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;
    private final Bundle fragmentBundle;
    private MovieDetailFragment detail_fragment;
    private TrailersDetailFragment trailers_fragment;
    private ReviewsDetailFragment reviews_fragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Bundle data) {
        super(fm);
        fragmentBundle = data;
        mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        //kk Question:
        // Does checking if not been loaded increases performance?
        switch (position) {
            case 0:
                if (detail_fragment==null){
                    detail_fragment = new MovieDetailFragment();
                    detail_fragment.setArguments(fragmentBundle);
                }
                return detail_fragment;
            case 1:
                if (trailers_fragment==null){
                    trailers_fragment = new TrailersDetailFragment();
                    trailers_fragment.setArguments(fragmentBundle);
                }
                return trailers_fragment;
            case 2:
                if (reviews_fragment==null){
                    reviews_fragment = new ReviewsDetailFragment();
                    reviews_fragment.setArguments(fragmentBundle);
                }
                return reviews_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
