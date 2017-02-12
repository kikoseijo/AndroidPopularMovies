package com.sunnyface.popularmovies;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.databinding.FragmentDetailBinding;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

import java.util.Locale;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class MovieDetailFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Movie movie;
    private boolean isTabletLayout;
    private boolean isFavorite;
    private Toast mToast;
    private FragmentDetailBinding binding;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();

        binding.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeFavoriteStatus();
            }
        });

        Typeface customTitleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Bold.ttf");

        if (arguments != null) {
            if (arguments.getBoolean("isTabletLayout")) {
                isTabletLayout = true;
                movie = arguments.getParcelable("movie");
            } else {
                movie = getActivity().getIntent().getExtras().getParcelable("movie");

                if (binding.movieTitle != null) {
                    assert movie != null;
                    binding.movieTitle.setText(movie.getTitle());
                    binding.movieTitle.setContentDescription(movie.getTitle());
                    binding.movieTitle.setTypeface(customTitleFont);
                }

            }
            assert movie != null;
            setMovieData(movie);
        }

        return binding.getRoot();
    }

    private void setFavouriteButton() {
        binding.buttonFavorite.setText(isFavorite ? R.string.remove_from_favorites : R.string.add_to_favourite);
    }

    private void changeFavoriteStatus() {
        if (isFavorite) {
            isFavorite = Utils.isMovieOnDatabase(getActivity(), (int) movie.getId());
            deleteMovieFromDatabase();
        } else {
            addMovieToDatabase();
        }
    }

    private void deleteMovieFromDatabase() {
        Integer isDeleted =
                getActivity().getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString((int) movie.getId())});

        if (isDeleted > 0) {
            showToast(getString(R.string.removed_from_favorites));
            isFavorite = !isFavorite;
            setFavouriteButton();
        } else {
            showToast(getString(R.string.problem_adding_to_favorites));
        }
    }

    private void addMovieToDatabase() {
        Utils.addMovieToDatabase(getActivity(), movie);
        showToast(getString(R.string.added_to_favorites));
        isFavorite = !isFavorite;
        setFavouriteButton();

    }

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    private void setMovieData(final Movie movie) {

        isFavorite = Utils.isMovieOnDatabase(getActivity(), (int) movie.getId());
        setFavouriteButton();

        // Date View
        String release_date = movie.getRelease_date();
        if (release_date.contains("-")) {
            release_date = release_date.split("-")[0];
        }

        binding.detailRelease.setText(release_date);
        binding.detailRelease.setContentDescription("Release Date " + release_date);

        // Rating View

        binding.detailRating.setText(String.format(Locale.ENGLISH,"%.1f", movie.getVote_average()) + "/10");
        binding.detailRating.setContentDescription("Rating " + movie.getVote_average() + "/ 10");

        // Details view
        binding.detailOverview.setText(movie.getOverview());
        binding.detailOverview.setContentDescription(movie.getOverview());

        Utils.imageViewObserver(binding.moviePoster, getActivity(), "cover", movie);

        if (isTabletLayout) {
            // Tablet Title View

            binding.movieTitle.setText(movie.getTitle());
            binding.movieTitle.setContentDescription(movie.getTitle());

        }

    }


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        // Must implement by Fragment.
    }


}
