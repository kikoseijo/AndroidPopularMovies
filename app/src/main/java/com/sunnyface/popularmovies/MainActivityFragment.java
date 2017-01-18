package com.sunnyface.popularmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.sunnyface.popularmovies.data.EndlessRecyclerOnScrollListener;
import com.sunnyface.popularmovies.data.MovieAdapter;
import com.sunnyface.popularmovies.data.MovieContract;
import com.sunnyface.popularmovies.libs.Utils;
import com.sunnyface.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiko Seijo on 14/01/2017.
 * by The Sunnyface.com.
 */

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<Movie> moviesArray = new ArrayList<>();
    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String MOVIES_KEY = "movies";
    private int page_num = 1;
    private MovieAdapter movieAdapter;
    private String sort_type;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ImageView disconnected_icon;
    private RequestQueue requestQueue;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(TAG, "onCreate: ");
        requestQueue = Volley.newRequestQueue(getActivity());

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView: ");
        final View thisView = inflater.inflate(R.layout.fragment_main, container, false);

        disconnected_icon = (ImageView) thisView.findViewById(R.id.no_connection);
        emptyView = (TextView) thisView.findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) thisView.findViewById(R.id.progress_bar);


        progressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                sort_type = savedInstanceState.getString(SORT_SETTING_KEY);
            }
            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                moviesArray = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                if (moviesArray != null && moviesArray.size() > 0) {
                    if (movieAdapter != null) {
                        movieAdapter.addMovies(moviesArray);
                    }
                }

            }
        } else {
            Log.i(TAG, "onCreateView: LL");
            sort_type = getString(R.string.sort_popularity);
        }

        /**
         * Number of columns depending on screen width.
         * TODO: Show 3 columns on mobile landscape.
         * */

//        Configuration config = thisView.getResources().getConfiguration();
//        if (config.smallestScreenWidthDp >= 600 && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
//            linearLayoutManager = new GridLayoutManager(getActivity(), 1);
//        } else if (config.smallestScreenWidthDp < 600 && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
//            linearLayoutManager = new GridLayoutManager(getActivity(), 3);
//        } else {
//            linearLayoutManager = new GridLayoutManager(getActivity(), 2);
//        }
        /** Number of columns depending on screen width. */
        Integer spanCount = getResources().getInteger(R.integer.movies_per_row);
        linearLayoutManager = new GridLayoutManager(getActivity(), spanCount);


        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(getActivity());
        recyclerView.setAdapter(movieAdapter);
        setOnClickListenerOnItems();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "onLoadMore: current_page: " + current_page);
                loadMoreMovies();
            }
        });

        boolean isConnected = Utils.isNetworkAvailable(getActivity());
        if (!isConnected) {
            Toast aToast = Toast.makeText(getActivity(), R.string.text_no_internet_msg, Toast.LENGTH_LONG);
            aToast.show();
            progressBar.setVisibility(View.GONE);
        }

        return thisView;
    }

    // MARK: Action MENU
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "onPrepareOptionsMenu: ");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem action_popularity = menu.findItem(R.id.action_sort_popularity);
        MenuItem action_rating = menu.findItem(R.id.action_sort_rating);
        MenuItem action_fav = menu.findItem(R.id.action_favorite);

        if (sort_type != null && sort_type.equals(getResources().getString(R.string.sort_popularity))) {
            if (action_popularity != null && !action_popularity.isChecked()) {
                action_popularity.setChecked(true);
            }
        } else if (sort_type != null && sort_type.equals(getResources().getString(R.string.sort_rating))) {
            if (action_rating != null && !action_rating.isChecked()) {
                action_rating.setChecked(true);
            }
        } else if (sort_type != null && sort_type.equals(getResources().getString(R.string.sort_favorites))) {
            if (action_fav != null && !action_fav.isChecked()) {
                action_fav.setChecked(true);
            }
        } else {
            sort_type = getResources().getString(R.string.sort_popularity);
            action_popularity.setChecked(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        int id = item.getItemId();
        page_num = 1;
        switch (id) {
            case R.id.action_sort_popularity:
                item.setChecked(!item.isChecked());
                sort_type = getResources().getString(R.string.sort_popularity);
                reloadMovies();
                return true;
            case R.id.action_sort_rating:
                item.setChecked(!item.isChecked());
                sort_type = getResources().getString(R.string.sort_rating);
                reloadMovies();
                return true;
            case R.id.action_favorite:
                item.setChecked(!item.isChecked());
                sort_type = getResources().getString(R.string.sort_favorites);
                reloadMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface Callback {
        void onItemSelected(Movie movie, View view);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putString(SORT_SETTING_KEY, sort_type);
        Log.i(TAG, "onSaveInstanceState: sort_type:: " + sort_type);
        if (moviesArray != null) {
            outState.putParcelableArrayList(MOVIES_KEY, moviesArray);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.i(TAG, "onViewStateRestored: ");
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                sort_type = savedInstanceState.getString(SORT_SETTING_KEY);
            }
            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                moviesArray = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                movieAdapter.addMovies(moviesArray);
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager = new GridLayoutManager(getActivity(), 1);

        }
    }


    private void loadMoreMovies() {
        Log.i(TAG, "loadMoreMovies: ");
        page_num += 1;
        reloadMovies();
    }

    // MARK: Build request to webservices using volley framework.

    private void loadMovieRequest() {
        Log.i(TAG, "loadMovieRequest: page_num->" + page_num);
        String JsonURL = Utils.buildMoviesUrl(sort_type, page_num);


        JsonObjectRequest arrayreq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray moviesJsonArray = response.getJSONArray("results");
                            List<Movie> results = new ArrayList<>();
                            for (int i = 0; i < moviesJsonArray.length(); i++) {
                                JSONObject movieObj = moviesJsonArray.getJSONObject(i);
                                Movie movie = Movie.deserialize(movieObj);
                                results.add(movie);
                            }
                            if (movieAdapter != null) {
                                if (page_num == 1) {
                                    movieAdapter.clear();
                                }
                                movieAdapter.addMovies(results);
                            }
                            if (page_num == 1) {
                                moviesArray = new ArrayList<>();
                            }
                            moviesArray.addAll(results);
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

    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        private final Context mContext;

        FetchFavoriteMoviesTask(Context context) {
            mContext = context;
            Log.i(TAG, "FetchFavoriteMoviesTask: ");
        }

        private List<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            ArrayList<Movie> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {

                if (movieAdapter != null) {
                    movieAdapter.clear();
                    movieAdapter.addMovies(movies);
                }
                moviesArray = new ArrayList<>();
                moviesArray.addAll(movies);
                displayResults();
            }
        }
    }


    private void displayResults() {

        Log.i(TAG, "displayResults: ");
        if (moviesArray == null || moviesArray.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            Toast toast = Toast.makeText(getActivity(), R.string.text_no_records_found, Toast.LENGTH_LONG);
            toast.show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void reloadMovies() {

        Log.i(TAG, "reloadMovies: ");

        if (Utils.isNetworkAvailable(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            disconnected_icon.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (sort_type.equals(getResources().getString(R.string.sort_favorites))) {
                new FetchFavoriteMoviesTask(getActivity()).execute();
            } else {
                if (sort_type == null || sort_type.equals("")) {
                    sort_type = getString(R.string.sort_popularity);
                }
                loadMovieRequest();
            }

        } else {
            Toast toast = Toast.makeText(getActivity(), R.string.text_no_internet_msg, Toast.LENGTH_LONG);
            toast.show();
            recyclerView.setVisibility(View.GONE);
            disconnected_icon.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListenerOnItems() {
        Log.i(TAG, "setOnClickListenerOnItems: ");
        MovieAdapter.OnItemClickListener onItemClickListener = new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position < movieAdapter.getItemCount()) {
                    Movie movie = (Movie) moviesArray.toArray()[position];
                    ((Callback) getActivity()).onItemSelected(movie, v);
                }
            }
        };
        movieAdapter.setOnItemClickListener(onItemClickListener);
    }


}