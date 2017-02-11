package com.sunnyface.popularmovies;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

/**
 * Created by smac on 14/01/2017.
 */

public class DetailActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Movie movie;
    private MenuItem menuItem;
    private boolean isTabletLayout;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();

        if (arguments != null) {
            if (arguments.getBoolean("isTabletLayout")) {
                isTabletLayout = true;
                movie = arguments.getParcelable("movie");
            } else {
                movie = getActivity().getIntent().getExtras().getParcelable("movie");

                Toolbar toolbar = (Toolbar) view.findViewById(R.id.action_bar);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);
                if (toolbarLayout != null) {
                    toolbarLayout.setTitle(movie.getTitle());
                    final ImageView header_image = (ImageView) view.findViewById(R.id.poster);
                    Utils.imageViewObserver(header_image, getActivity(), "back", movie);
                }

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


    private void setMovieData(final Movie movie, final View view) {

        final ImageView imageView = (ImageView) view.findViewById(R.id.moviePoster);

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
                    String imgUrl = movie.getImageUrl(imageView.getWidth(), "cover").toString();

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
