package com.sunnyface.popularmovies.adapters;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunnyface.popularmovies.R;
import com.sunnyface.popularmovies.models.Review;

import java.util.Collection;

/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private static final String TAG = ReviewsAdapter.class.getSimpleName();
    private Collection<Review> reviews;
    private final ReviewsAdapterOnClickHandler mClickHandler;


    public interface ReviewsAdapterOnClickHandler
    {
        void onClick(String reviewDetailUrl);
    }

    public ReviewsAdapter(ReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return (null != reviews ? reviews.size() : 0);
    }

    public void addReviews(Collection<Review> reviews)
    {
        if (this.reviews == null) {
            this.reviews = reviews;
        } else {
            this.reviews.addAll(reviews);
        }
        this.notifyDataSetChanged();
    }

    public void clear()
    {
        if (this.reviews != null) {
            int size = this.reviews.size();
            this.reviews.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_review, null);
        //Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "  + viewHolderCount);
        return new ReviewsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);
        final Review review = (Review) reviews.toArray()[position];

        holder.bind(review);
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView authorTv;
        TextView commentsTv;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            authorTv = (TextView) itemView.findViewById(R.id.tv_review_author);
            commentsTv = (TextView) itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        void bind(Review review) {
            authorTv.setText(review.getAuthor());
            String contentText =  review.getContent();
            Integer maxLength =  180 < contentText.length() ? 180 : contentText.length();
            commentsTv.setText(review.getContent().substring(0, maxLength ) + "...");
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final Review review = (Review) reviews.toArray()[adapterPosition];
            mClickHandler.onClick(review.getUrl());
        }
    }
}
