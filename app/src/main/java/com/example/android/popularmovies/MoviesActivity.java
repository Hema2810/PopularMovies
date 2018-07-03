package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MoviesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>,
        MoviesAdapter.onMovieClickedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //This app uses the themoviedb.org API to get the movies list and details

    private static final int SCREEN_WIDTH600 = 600;
    private static final int SCREEN_WIDTH720 = 720;
    private static final String SORT_KEY = "sort";
    private static final String DEFAULT_SORT = "popular";
    private static final String POPULAR = "popular";
    private static final String KEY = "Movies";
    private static final String BUNDLE_KEY = "query";
    private static final String POPULAR_QUERY = "https://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.API_KEY;
    private static final String TOP_QUERY = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.API_KEY;
    private static final String FAVORITE_SORT = "favorite";
    private static final int MOVIE_LOADER = 22;
    public static int NUM_GRID_COLUMNS = 2;
    final MoviesAdapter mAdapter = new MoviesAdapter(this, this);
    String mCurrentSortKey;
    ConnectivityManager connectivityManager;
    ConnectionReceiver mReceiver = new ConnectionReceiver();
    IntentFilter receiverFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private ArrayList<MovieInfo> mMovies = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private ArrayList<MovieInfo> favouriteMovies = new ArrayList<>();
    private MovieDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);
        int screenSize = (int) (this.getResources().getDisplayMetrics().widthPixels / this.getResources().getDisplayMetrics().density);

        if (screenSize > SCREEN_WIDTH720) {
            NUM_GRID_COLUMNS = 4;
        }
        if (screenSize > SCREEN_WIDTH600) {
            NUM_GRID_COLUMNS = 3;
        } else {
            NUM_GRID_COLUMNS = 2;
        }

        mDb = MovieDatabase.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.rv_movies);
        layoutManager = new GridLayoutManager(this, NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        SharedPreferences sharedPreferences =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        String sortBy = sharedPreferences.getString(SORT_KEY, DEFAULT_SORT);
        mCurrentSortKey = sortBy;
        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY)) {

            populateTheMovieList(mCurrentSortKey);

        } else {
            mMovies = savedInstanceState.getParcelableArrayList(KEY);
            mAdapter.setMovies(mMovies);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private void getJsonResponse(String queryUrl) {

        if (queryUrl != null) {
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

    }

    void populateTheMovieList(String sortBy) {
        if (sortBy.equals(FAVORITE_SORT)) {

            MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            viewModel.getMovies().observe(this, new Observer<List<MovieInfo>>() {
                @Override
                public void onChanged(@Nullable List<MovieInfo> movieInfos) {
                    if (movieInfos != null) {
                        favouriteMovies.clear();
                        favouriteMovies.addAll(movieInfos);

                    }
                    mAdapter.setMovies(favouriteMovies);
                    mRecyclerView.setAdapter(mAdapter);
                }
            });

        } else {

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                String queryUrl = getQueryUrl(sortBy);
                getJsonResponse(queryUrl);
                try {
                    unregisterReceiver(mReceiver);
                } catch (IllegalArgumentException e) {

                }


            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                registerReceiver(mReceiver, receiverFilter);

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mCurrentSortKey.equals(FAVORITE_SORT)) {
            outState.putParcelableArrayList(KEY, mMovies);
        }
    }

    private String getQueryUrl(String sortBy) {
        if (sortBy.equals(FAVORITE_SORT)) {
            return null;
        }
        if (sortBy.equals(POPULAR)) {
            return POPULAR_QUERY;
        } else {
            return TOP_QUERY;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String queryUrl = null;
        mCurrentSortKey = sharedPreferences.getString(key, DEFAULT_SORT);
        populateTheMovieList(mCurrentSortKey);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);

    }

    private ArrayList<MovieInfo> parseJson(String jsonString) {

        String baseUrl = "http://image.tmdb.org/t/p/";
        String imageSize = "w185";
        String resultsKey = "results";
        String posterPathKey = "poster_path";
        String originalTitleKey = "original_title";
        String releaseDateKey = "release_date";
        String synopsisKey = "overview";
        String ratingKey = "vote_average";
        String idString = "id";
        ArrayList<MovieInfo> mMoviesList = new ArrayList<>();

        try {
            JSONObject movie = new JSONObject(jsonString);
            JSONArray results = movie.getJSONArray(resultsKey);
            if (results != null) {

                for (int i = 0; i < results.length(); i++) {

                    String image = baseUrl + imageSize + results.getJSONObject(i).getString(posterPathKey);
                    int id = results.getJSONObject(i).getInt(idString);
                    String originalTitle = results.getJSONObject(i).getString(originalTitleKey);
                    String releaseDate = results.getJSONObject(i).getString(releaseDateKey);
                    String synopsis = results.getJSONObject(i).getString(synopsisKey);
                    String rating = results.getJSONObject(i).getString(ratingKey);
                    MovieInfo currentMovieInfo = new MovieInfo(id, originalTitle, image, releaseDate, synopsis, rating);
                    mMoviesList.add(currentMovieInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMovies = mMoviesList;
        return mMoviesList;

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

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        if (!mCurrentSortKey.equals(FAVORITE_SORT)) {
            mAdapter.setMovies(parseJson(data));
            mRecyclerView.setAdapter(mAdapter);
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
    public void onMovieClicked(int clickedMovieIndex) {
        // Toast.makeText(this, "mainActivity clicked movie index "+ clickedMovieIndex, Toast.LENGTH_SHORT).show();
        Intent detailIntent = new Intent(this, MovieDetail.class);
        if (mCurrentSortKey.equals(FAVORITE_SORT)) {
            detailIntent.putExtra(Intent.EXTRA_TEXT, favouriteMovies.get(clickedMovieIndex));
        } else {
            detailIntent.putExtra(Intent.EXTRA_TEXT, mMovies.get(clickedMovieIndex));
        }
        startActivity(detailIntent);


    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        loader.forceLoad();
    }

    class ConnectionReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (intent.getAction() != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    String queryUrl = getQueryUrl(mCurrentSortKey);
                    getJsonResponse(queryUrl);
                    unregisterReceiver(mReceiver);
                }

            }
        }
    }


}



