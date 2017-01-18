package com.sunnyface.popularmovies;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.data.MovieDbHelper;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class DetailActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = DetailActivityFragment.class.getSimpleName();
    private Movie movie;
    private boolean isTabletLayout;
    private boolean isFavorite;
    private Button favBtn;
    private Toast mToast;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();

        favBtn = (Button) view.findViewById(R.id.button_favorite);
        favBtn.setOnClickListener(new View.OnClickListener() {
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

                Toolbar toolbar = (Toolbar) view.findViewById(R.id.action_bar);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);
                if (toolbarLayout != null) {
                    toolbarLayout.setTitle(movie.getTitle());
                    final ImageView header_image = (ImageView) view.findViewById(R.id.poster);
                    Utils.imageViewObserver(header_image, getActivity(), "back", movie);
                }
                //}

                final TextView titleView = (TextView) view.findViewById(R.id.movie_title);
                if (titleView != null) {
                    titleView.setText(movie.getTitle());
                    titleView.setContentDescription(movie.getTitle());
                }

            }
            assert movie != null;
            setMovieData(movie, view);
        }

        return view;
    }

    private void setFavouriteButton() {
        favBtn.setText(isFavorite ? R.string.remove_from_favorites : R.string.add_to_favourite);
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

        final ImageView imageView = (ImageView) view.findViewById(R.id.moviePoster);

        isFavorite = Utils.isMovieOnDatabase(getActivity(), (int) movie.getId());
        setFavouriteButton();

        // Date View
        String release_date = movie.getRelease_date();
        if (release_date.contains("-")) {
            release_date = release_date.split("-")[0];
        }
        final TextView release_year = (TextView) view.findViewById(R.id.detail_release);
        release_year.setText(release_date);
        release_year.setContentDescription("Release Date " + release_date);

        // Rating View
        final TextView vote = (TextView) view.findViewById(R.id.detail_rating);
        vote.setText(String.format("%.1f", movie.getVote_average()) + "/10");
        vote.setContentDescription("Rating " + movie.getVote_average() + "/ 10");

        // Details view
        final TextView overview = (TextView) view.findViewById(R.id.detail_overview);
        overview.setText(movie.getOverview());
        overview.setContentDescription(movie.getOverview());

        if (isTabletLayout) {
            // Tablet Title View
            TextView movie_title = (TextView) view.findViewById(R.id.movieTitle);
            movie_title.setText(movie.getTitle());
            movie_title.setContentDescription(movie.getTitle());


            ViewTreeObserver vto = imageView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = imageView.getWidth();
                    //Log.d("TEST", "Width = " + width + " Height = " + imageView.getHeight());
                    String imgUrl = movie.getImageUrl(width, "cover").toString();

                    Picasso.with(getActivity())
                            .load(imgUrl)
                            .into(imageView,
                                    PicassoPalette.with(imgUrl, imageView)
                                            .use(PicassoPalette.Profile.VIBRANT)
                                            .intoBackground(view, PicassoPalette.Swatch.RGB)
                                            .intoTextColor(overview, PicassoPalette.Swatch.TITLE_TEXT_COLOR)
                                            .intoTextColor(release_year, PicassoPalette.Swatch.TITLE_TEXT_COLOR)
                                            .intoTextColor(vote, PicassoPalette.Swatch.TITLE_TEXT_COLOR));

                    ViewTreeObserver obs = imageView.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                }
            });


        } else {
            Utils.imageViewObserver(imageView, getActivity(), "cover", movie);
        }


    }


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        // Must implement by Fragment.
    }


}
