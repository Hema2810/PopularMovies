package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>, SharedPreferences.OnSharedPreferenceChangeListener {

    //This app uses the themoviedb.org API to get the movies list and details

    public static int NUM_GRID_COLUMNS = 2;
    private static final int SCREEN_WIDTH600 = 600;
    private static final int SCREEN_WIDTH720 = 720;
    private static final String SORT_KEY = "sort";
    private static final String DEFAULT_SORT = "popular";
    private static final String KEY = "Movies";
    private static final String BUNDLE_KEY = "query";
    private static final String POPULAR_QUERY = "https://api.themoviedb.org/3/movie/popular?api_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String TOP_QUERY = "https://api.themoviedb.org/3/movie/top_rated?api_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    private ArrayList<MovieInfo> mMovies = new ArrayList<>();
    private static final int MOVIE_LOADER = 22;
    private GridLayoutManager layoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        int screenSize = (int) (this.getResources().getDisplayMetrics().widthPixels / this.getResources().getDisplayMetrics().density);

        if (screenSize>SCREEN_WIDTH720){
            NUM_GRID_COLUMNS=4;}
        if (screenSize > SCREEN_WIDTH600){
            NUM_GRID_COLUMNS = 3;}
        else {
            NUM_GRID_COLUMNS = 2;
        }


        mRecyclerView = findViewById(R.id.rv_movies);
        layoutManager = new GridLayoutManager(this, NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        SharedPreferences sharedPreferences =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY)) {
            String sortBy = sharedPreferences.getString(SORT_KEY, DEFAULT_SORT);
            String queryUrl = getQueryUrl(sortBy);
            updateUI(queryUrl);
        } else {
            mMovies = savedInstanceState.getParcelableArrayList(KEY);
            MoviesAdapter mAdapter = new MoviesAdapter(mMovies);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY, mMovies);
    }

    private String getQueryUrl(String sortBy) {

        if (sortBy.equals(DEFAULT_SORT)) {
            return POPULAR_QUERY;
        } else {
            return TOP_QUERY;
        }


    }

    private void updateUI(String queryUrl) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(BUNDLE_KEY, queryUrl);
        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER);

        if (loader == null) {
            loaderManager.initLoader(MOVIE_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, queryBundle, this);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String queryUrl = null;
        if (key.equals(getString(R.string.sort_key)))
            queryUrl = getQueryUrl(sharedPreferences.getString(key, DEFAULT_SORT));
        mMovies.clear();
        updateUI(queryUrl);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.preference_settings) {
            Intent preferenceSettingsIntent = new Intent(this, Settings.class);
            startActivity(preferenceSettingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovieImages(String jsonString) {

        String baseUrl = "http://image.tmdb.org/t/p/";
        String imageSize = "w185";
        String resultsKey ="results";
        String posterPathKey = "poster_path";
        String originalTitleKey ="original_title";
        String releaseDateKey ="release_date";
        String synopsisKey="overview";
        String ratingKey="vote_average";

        try {
            JSONObject movie = new JSONObject(jsonString);
            JSONArray results = movie.getJSONArray(resultsKey);
            if (results != null) {

                for (int i = 0; i < results.length(); i++) {

                    String image = baseUrl + imageSize + results.getJSONObject(i).getString(posterPathKey);
                    String originalTitle = results.getJSONObject(i).getString(originalTitleKey);
                    String releaseDate = results.getJSONObject(i).getString(releaseDateKey);
                    String synopsis = results.getJSONObject(i).getString(synopsisKey);
                    String rating = results.getJSONObject(i).getString(ratingKey);
                    MovieInfo currentMovieInfo = new MovieInfo(originalTitle, image, releaseDate, synopsis, rating);
                    mMovies.add(currentMovieInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(BUNDLE_KEY);
                String jsonString = "";

                try {
                    jsonString = MovieUtils.httpRequest(new URL(searchQueryUrlString));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return jsonString;

            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        loadMovieImages(data);
        MoviesAdapter mAdapter = new MoviesAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        loader.forceLoad();
    }


}

