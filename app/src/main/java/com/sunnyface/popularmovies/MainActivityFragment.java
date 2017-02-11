package com.sunnyface.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.sunnyface.popularmovies.libs.MovieAdapter;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by smac on 14/01/2017.
 */

public class MainActivityFragment extends Fragment implements EndlessRecyclerViewAdapter.RequestToLoadMoreListener {

    private Collection<Movie> moviesArray = new ArrayList<>();
    private int page_num = 1;
    private MovieAdapter movieAdapter;
    private String sort_type;
    private ProgressBar progressBar;
    private EndlessRecyclerViewAdapter endlessRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isConnected;
    private RecyclerView moviesRecyclerView;
    private ImageView disconnected_icon;
    private RequestQueue requestQueue;
    private TextView empty_message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View thisView = inflater.inflate(R.layout.fragment_main, container, false);

        disconnected_icon = (ImageView) thisView.findViewById(R.id.no_connection);
        movieAdapter = new MovieAdapter(getActivity(), null);
        endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getActivity(), movieAdapter, this);
        empty_message = (TextView) thisView.findViewById(R.id.empty_text);

        /** Number of columns depending on screen width. */
        Integer spanCount = getResources().getInteger(R.integer.movies_per_row);
        linearLayoutManager = new GridLayoutManager(getActivity(), spanCount);


        moviesRecyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        moviesRecyclerView.setLayoutManager(linearLayoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(endlessRecyclerViewAdapter);

        //Progress bar for slow connectivity.
        progressBar = (ProgressBar) thisView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        isConnected = Utils.isNetworkAvailable(getActivity());
        sort_type = getResources().getString(R.string.sort_popularity);

        if (!isConnected) {
            Toast aToast = Toast.makeText(getActivity(), R.string.text_no_internet_msg, Toast.LENGTH_LONG);
            aToast.show();
            progressBar.setVisibility(View.INVISIBLE);
            empty_message.setVisibility(View.VISIBLE);
        }
        return thisView;
    }

    // MARK: Action MENU
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem action_popularity = menu.findItem(R.id.action_sort_popularity);
        MenuItem action_rating = menu.findItem(R.id.action_sort_rating);

        Log.i("SortOnCreate:", "onCreateOptionsMenu: " + sort_type);

        if (sort_type != null && sort_type.contentEquals(getResources().getString(R.string.sort_popularity))) {
            if (action_popularity != null && !action_popularity.isChecked()) {
                action_popularity.setChecked(true);
            }
        } else if (sort_type != null && sort_type.contentEquals(getResources().getString(R.string.sort_rating))) {
            if (action_rating != null && !action_rating.isChecked()) {
                action_rating.setChecked(true);
            }
        } else {
            sort_type = getResources().getString(R.string.sort_popularity);
            action_popularity.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    clearMovies();
                }
                sort_type = getResources().getString(R.string.sort_popularity);
                reloadMovies();
                return true;
            case R.id.action_sort_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    clearMovies();
                }
                sort_type = getResources().getString(R.string.sort_rating);
                reloadMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getResources().getString(R.string.sort_type), sort_type);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            sort_type = savedInstanceState.getString(getResources().getString(R.string.sort_type));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager = new GridLayoutManager(getActivity(), 1);

        }
    }

    @Override
    public void onLoadMoreRequested() {
        //page_num += 1;    Stage2 - Infinite load.
        reloadMovies();
    }

    // MARK: Build request to webservices using volley framework.

    private void loadMovieRequest(int page_num) {

        String JsonURL = Utils.buildMoviesUrl(sort_type, page_num);
        Log.i("MainFragment", "loadMovieRequest: JsonURL" + JsonURL);
        JsonObjectRequest arrayreq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Retrieves first JSON object in outer array
                            //SONObject results = response.getJSONObject("resutls");
                            // Retrieves "colorArray" from the JSON object

                            JSONArray moviesJsonArray = response.getJSONArray("results");
                            ArrayList<Movie> movies = new ArrayList<>();
                            for (int i = 0; i < moviesJsonArray.length(); i++) {
                                JSONObject movieObj = moviesJsonArray.getJSONObject(i);
                                Movie movie = Movie.deserialize(movieObj);
                                movies.add(movie);
                            }

                            if (movies != null) {
                                moviesArray.addAll(movies);
                                movieAdapter.addMovies(movies);
                                endlessRecyclerViewAdapter.onDataReady(true);
                                setOnClickListenerOnItems(moviesArray);
                            }
                            progressBar.setVisibility(View.GONE);
                            movieAdapter.notifyDataSetChanged();


                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivityFragment", "Movie Request using Volley Error");
                    }
                }
        );
        // Adds the JSON array request "arrayreq" to the request queue
        requestQueue.add(arrayreq);
    }

    private void clearMovies() {
        moviesArray.clear();
        movieAdapter.clear();
        page_num = 1;
    }

    private void reloadMovies() {
        if (Utils.isNetworkAvailable(getActivity())) {
            empty_message.setVisibility(View.GONE);
            disconnected_icon.setVisibility(View.GONE);
            moviesRecyclerView.setVisibility(View.VISIBLE);
            loadMovieRequest(page_num);
        } else {
            Toast aToast = Toast.makeText(getActivity(), R.string.text_no_internet_msg, Toast.LENGTH_LONG);
            aToast.show();
            empty_message.setVisibility(View.VISIBLE);
            moviesRecyclerView.setVisibility(View.GONE);
            disconnected_icon.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListenerOnItems(final Collection<Movie> movies) {
        MovieAdapter.OnItemClickListener onItemClickListener = new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Movie movie = (Movie) moviesArray.toArray()[position];
                ((Callback) getActivity()).onItemSelected(movie, v);
            }
        };
        movieAdapter.setOnItemClickListener(onItemClickListener);
    }


    public interface Callback {
        void onItemSelected(Movie movie, View view);
    }

}