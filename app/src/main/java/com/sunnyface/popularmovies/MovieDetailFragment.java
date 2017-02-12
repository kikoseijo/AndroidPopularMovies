package com.sunnyface.popularmovies;

import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.databinding.FragmentDetailBinding;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class MovieDetailFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = MovieDetailFragment.class.getSimpleName();
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

        if (arguments != null) {
            if (arguments.getBoolean("isTabletLayout")) {
                isTabletLayout = true;
                movie = arguments.getParcelable("movie");
            } else {
                movie = getActivity().getIntent().getExtras().getParcelable("movie");

                if (binding.movieTitle != null) {
                    binding.movieTitle.setText(movie.getTitle());
                    binding.movieTitle.setContentDescription(movie.getTitle());
                }

            }
            assert movie != null;
            setMovieData(movie, binding.getRoot());
        }

        return binding.getRoot();
    }

    private void setFavouriteButton() {
        binding.buttonFavorite.setText(isFavorite ? R.string.remove_from_favorites : R.string.add_to_favourite);
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
                getActivity().getContentResolver().delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString((int) movie.getId())});

        if (isDeleted>0){
            showToast(getString(R.string.removed_from_favorites));
            isFavorite = !isFavorite;
            setFavouriteButton();
        } else {
            showToast(getString(R.string.problem_adding_to_favorites));
        }
    }

    private void addMovieToDatabase(){
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVote_count());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());

        Uri rUri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        Log.i(TAG, "rUri: " + rUri);
        showToast(getString(R.string.added_to_favorites));
        isFavorite = !isFavorite;
        setFavouriteButton();

    }

    private void showToast(String msg){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    private void setMovieData(final Movie movie, final View view) {



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

        binding.detailRating.setText(String.format("%.1f", movie.getVote_average()) + "/10");
        binding.detailRating.setContentDescription("Rating " + movie.getVote_average() + "/ 10");

        // Details view
        Log.i(TAG, "setMovieData: overview" + movie.getOverview());
        binding.detailOverview.setText(movie.getOverview());
        binding.detailOverview.setContentDescription(movie.getOverview());

        Log.i(TAG, "setMovieData: kkkkkkkkkkkkk HEIGHT" + binding.detailOverview.getHeight());
        Log.i(TAG, "setMovieData: kkkkkkkkkkkkk WIDTH" + binding.detailOverview.getWidth());

        if (isTabletLayout) {
            // Tablet Title View

            binding.movieTitle.setText(movie.getTitle());
            binding.movieTitle.setContentDescription(movie.getTitle());


            ViewTreeObserver vto = binding.moviePoster.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = binding.moviePoster.getWidth();
                    //Log.d("TEST", "Width = " + width + " Height = " + imageView.getHeight());
                    String imgUrl = movie.getImageUrl(width, "cover").toString();
                    Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
                    Picasso.with(getActivity())
                            .load(imgUrl)
                            .error(transparentDrawable)
                            .into(binding.moviePoster,
                                    PicassoPalette.with(imgUrl, binding.moviePoster)
                                            .use(PicassoPalette.Profile.VIBRANT)
                                            .intoBackground(view, PicassoPalette.Swatch.RGB)
                                            //.intoTextColor(binding.detailOverview, PicassoPalette.Swatch.TITLE_TEXT_COLOR)
                                            .intoTextColor(binding.detailRelease, PicassoPalette.Swatch.TITLE_TEXT_COLOR)
                                            .intoTextColor(binding.detailRating, PicassoPalette.Swatch.TITLE_TEXT_COLOR));

                    ViewTreeObserver obs = binding.moviePoster.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                }
            });


        } else {
            Utils.imageViewObserver(binding.moviePoster, getActivity(), "cover", movie);
        }


    }


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        // Must implement by Fragment.
    }


}
