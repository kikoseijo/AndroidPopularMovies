package com.sunnyface.popularmovies.adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunnyface.popularmovies.R;
import com.sunnyface.popularmovies.models.Trailer;

import java.util.Collection;


/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private Collection<Trailer> trailers;
    private final TrailersAdapterOnClickHandler mClickHandler;


    public interface TrailersAdapterOnClickHandler
    {
        void onClick(String reviewDetailUrl);
    }

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return (null != trailers ? trailers.size() : 0);
    }

    public void addTrailers(Collection<Trailer> trailers)
    {
        if (this.trailers == null) {
            this.trailers = trailers;
        } else {
            this.trailers.addAll(trailers);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_trailer, null);
        //Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "  + viewHolderCount);
        return new TrailerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);
        final Trailer review = (Trailer) trailers.toArray()[position];

        holder.bind(review);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView titleTV;
        final TextView sizeTV;
        final TextView tipoTV;
        final ImageView imgView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.trailer_title);
            sizeTV = (TextView) itemView.findViewById(R.id.trailer_size);
            tipoTV = (TextView) itemView.findViewById(R.id.trailer_type);
            imgView = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        void bind(Trailer trailer) {

            titleTV.setText(trailer.getName());
            titleTV.setContentDescription(trailer.getName());

            sizeTV.setText(trailer.getSite() + " " + trailer.getSize());
            sizeTV.setContentDescription(trailer.getSite() + " " + trailer.getSize());

            tipoTV.setText(trailer.getType());
            tipoTV.setContentDescription(trailer.getType());
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            Picasso.with(itemView.getContext()) //
                    .load("http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg") //
                    .error(transparentDrawable)
                    .into(imgView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final Trailer review = (Trailer) trailers.toArray()[adapterPosition];
            mClickHandler.onClick(review.getUrl());
        }
    }
}
