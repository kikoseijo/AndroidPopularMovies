package com.sunnyface.popularmovies.libs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.R;
import com.sunnyface.popularmovies.models.Movie;

import java.util.Collection;

/**
 * Created by smac on 14/01/2017.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    OnItemClickListener mItemClickListener;
    private Collection<Movie> movies;
    private Context context;

    public MovieAdapter(Context context, Collection<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_row, null);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, int i) {
        final Movie movie = (Movie) movies.toArray()[i];

        int viewWidth = movieViewHolder.imageView.getWidth();
        String imgUrl = movie.getImageUrl(342, "cover").toString();
        //Download image using picasso library
        Picasso.with(context)
                .load(imgUrl)
                .into(movieViewHolder.imageView,
                        PicassoPalette.with(imgUrl, movieViewHolder.imageView)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoBackground(movieViewHolder.movieLayout, PicassoPalette.Swatch.RGB)
                                .intoTextColor(movieViewHolder.titleView, PicassoPalette.Swatch.TITLE_TEXT_COLOR));

        //Setting text view title
        movieViewHolder.titleView.setText(movie.getTitle());
        movieViewHolder.titleView.setContentDescription(movie.getTitle());

    }

    @Override
    public int getItemCount() {
        return (null != movies ? movies.size() : 0);
    }

    public void addMovies(Collection<Movie> movies) {
        if (this.movies == null) {
            this.movies = movies;
        } else {
            this.movies.addAll(movies);
        }
    }

    public void clear() {
        if (this.movies != null) {
            int size = this.movies.size();
            this.movies.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout movieLayout;
        protected LinearLayout movieHolder;
        protected LinearLayout movieTitleHolder;
        protected ImageView imageView;
        protected TextView titleView;

        public MovieViewHolder(View view) {
            super(view);

            this.movieLayout = (LinearLayout) view.findViewById(R.id.movie_layout);
            this.movieHolder = (LinearLayout) view.findViewById(R.id.movie_holder);
            this.movieTitleHolder = (LinearLayout) view.findViewById(R.id.movie_title_holder);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.titleView = (TextView) view.findViewById(R.id.title);
            this.movieLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }
}