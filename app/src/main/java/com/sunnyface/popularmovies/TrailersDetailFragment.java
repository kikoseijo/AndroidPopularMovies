package com.sunnyface.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sunnyface.popularmovies.adapters.TrailersAdapter;
import com.sunnyface.popularmovies.databinding.TabTrailersBinding;
import com.sunnyface.popularmovies.extensions.SimpleDividerItemDecoration;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;
import com.sunnyface.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiko Seijo on 11/02/2017.
 * by The Sunnyface.com.
 */

public class TrailersDetailFragment extends Fragment implements TrailersAdapter.TrailersAdapterOnClickHandler {

    private static final String TAG = ReviewsDetailFragment.class.getSimpleName();
    private ArrayList<Trailer> trailersArray = new ArrayList<>();
    private Movie movie;
    private TrailersAdapter mAdapter;
    private TabTrailersBinding binding;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(TAG, "onCreate: ");
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tab_trailers, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.getBoolean("isTabletLayout")) {
                movie = arguments.getParcelable("movie");
            } else {
                movie = getActivity().getIntent().getExtras().getParcelable("movie");
            }
        }
        RecyclerView mRecyclerView = binding.rvTrailers;
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TrailersAdapter(this);
        mRecyclerView.setAdapter(mAdapter);



        boolean isConnected = Utils.isNetworkAvailable(getActivity());
        if (!isConnected) {
            Toast.makeText(getActivity(), R.string.text_no_internet_msg, Toast.LENGTH_LONG).show();
            binding.pbLoadingIndicator.setVisibility(View.GONE);
        } else {
            loadReviewsRequest();
        }


        return binding.getRoot();
    }


    private void loadReviewsRequest() {

        String JsonURL = Utils.buildStage2Url(movie.getId(), "videos");

        JsonObjectRequest arrayreq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray moviesJsonArray = response.getJSONArray("results");
                            List<Trailer> results = new ArrayList<>();
                            for (int i = 0; i < moviesJsonArray.length(); i++) {
                                JSONObject movieObj = moviesJsonArray.getJSONObject(i);
                                //Log.i(TAG, "onResponse: " + movieObj);
                                Trailer trailer = new Trailer(movieObj);
                                results.add(trailer);
                            }
                            if (mAdapter != null) {
                                mAdapter.addTrailers(results);
                            }

                            trailersArray = new ArrayList<>();
                            trailersArray.addAll(results);
                            //rev.addAll(results);
                            displayResults();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivityFragment", "Movie Request using Volley Error");
                    }
                }
        );
        requestQueue.add(arrayreq);
    }

    private void displayResults() {

        Log.i(TAG, "displayResults: ");
        if (trailersArray == null || trailersArray.isEmpty()) {
            binding.rvTrailers.setVisibility(View.GONE);
            binding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), R.string.text_no_records_found, Toast.LENGTH_LONG).show();
        } else {
            binding.rvTrailers.setVisibility(View.VISIBLE);
            binding.tvErrorMessageDisplay.setVisibility(View.GONE);
        }
        binding.pbLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onClick(String trailerDetailUrl) {
        Context context = getContext();
        Toast.makeText(context, trailerDetailUrl, Toast.LENGTH_SHORT).show();
        Utils.openWebViewWithUrlString(getContext(),trailerDetailUrl);
    }


}
