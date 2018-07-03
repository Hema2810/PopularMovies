package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>, TrailerAdapter.onTrailerClickedListener {


    private static final String BUNDLE_KEY = "video/review";
    private static final int TRAILER_LOADER = 28;
    private static final int REVIEW_LOADER = 26;
    private static final String REVIEW_STATE_KEY = "Review Open";
    private static final String NO_REVIEWS = "Sorry! no reviews available!";

    final TrailerAdapter mTrailerAdapter = new TrailerAdapter(this, this);
    MovieInfo mMovieInfo;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.release_date_tv)
    TextView releaseDate;
    @BindView(R.id.synopsis_tv)
    TextView synopsis;
    @BindView(R.id.user_rating_tv)
    TextView userRating;
    @BindView(R.id.movie_poster_iv)
    ImageView posterImage;
    @BindView(R.id.Favorite)
    ImageView favoriteButton;
    @BindView(R.id.tv_reviewLabel)
    TextView review;
    @BindView(R.id.review_text)
    TextView reviewText;
    MovieDatabase mDb;
    ConnectivityManager connectivityManager;
    ConnectionReceiver mReceiver = new ConnectionReceiver();
    IntentFilter receiverFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    String TRAILER_API;
    String REVIEW_API;
    private boolean isReviewOpen = false;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private List<TrailerInfo> mTrailers = new ArrayList<TrailerInfo>();
    private List<ReviewInfo> mReviews = new ArrayList<ReviewInfo>();
    private int currentId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(REVIEW_STATE_KEY, isReviewOpen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        mDb = MovieDatabase.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.rv_trailer);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        String pattern = "yyyy-MM-dd";
        String outPattern = "MMM dd yyyy";


        Intent parentActivityIntent = getIntent();
        if (parentActivityIntent != null) {
            if (parentActivityIntent.getExtras() != null) {
                mMovieInfo = parentActivityIntent.getParcelableExtra(Intent.EXTRA_TEXT);
            }

        }
        currentId = mMovieInfo.getId();
        TRAILER_API = "https://api.themoviedb.org/3/movie/" + currentId + "/videos?api_key=" + BuildConfig.API_KEY;

        REVIEW_API = "https://api.themoviedb.org/3/movie/" + currentId + "/reviews?api_key=" + BuildConfig.API_KEY;


        getJsonResponse(TRAILER_API, TRAILER_LOADER);

        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEW_STATE_KEY)) {
            isReviewOpen = savedInstanceState.getBoolean(REVIEW_STATE_KEY);
            if (isReviewOpen) {
                reviewText.setVisibility(View.VISIBLE);
                loadReview();
            } else {
                reviewText.setVisibility(View.GONE);
            }
        }


        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReviewOpen) {
                    reviewText.setVisibility(View.GONE);
                    isReviewOpen = false;
                } else {
                    loadReview();
                }


            }
        });

        title.setText(mMovieInfo.getMovieTitle());
        String rating = mMovieInfo.getRating() + " /10";
        userRating.setText(rating);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        try {
            Date date = simpleDateFormat.parse(mMovieInfo.getReleaseDate());
            SimpleDateFormat simpleDateFormatOutput = new SimpleDateFormat(outPattern, Locale.US);
            String dateString = simpleDateFormatOutput.format(date);
            releaseDate.setText(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        synopsis.setText(mMovieInfo.getSynopsis());

        Picasso.with(this)
                .load(mMovieInfo.getThumbnailImage())
                .into(posterImage);


        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(@Nullable List<MovieInfo> movieInfos) {
                final int resource;
                boolean found = false;
                for (int i = 0; i < movieInfos.size(); i++) {
                    if (movieInfos.get(i).getId() == currentId) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    mMovieInfo.setFavorite(1);
                    resource = android.R.drawable.btn_star_big_on;
                } else {
                    mMovieInfo.setFavorite(0);
                    resource = android.R.drawable.btn_star_big_off;
                }
                favoriteButton.setImageResource(resource);

            }
        });

    }

    private void loadReview() {

        getJsonResponse(REVIEW_API, REVIEW_LOADER);
        reviewText.setVisibility(View.VISIBLE);
        isReviewOpen = true;


    }

    public void favoriteClicked(View v) {

        if (mMovieInfo.getFavorite() == 0) {
            mMovieInfo.setFavorite(1);
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(mMovieInfo);

                }
            });

        } else {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            mMovieInfo.setFavorite(0);

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(mMovieInfo);
                }
            });

        }

    }

    private void getJsonResponse(String queryUrl, int loaderId) {

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if (queryUrl != null) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString(BUNDLE_KEY, queryUrl);
                LoaderManager loaderManager = getSupportLoaderManager();

                Loader<String> loader = loaderManager.getLoader(loaderId);

                if (loader == null) {
                    loaderManager.initLoader(loaderId, queryBundle, this);
                } else {
                    loaderManager.restartLoader(loaderId, queryBundle, this);
                }
            }
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {

            }


        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
            registerReceiver(mReceiver, receiverFilter);

        }


    }

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
        String json = data;
        if (loader.getId() == TRAILER_LOADER) {
            mTrailers = parseTrailerJson(data);
            mTrailerAdapter.setTrailers(mTrailers);
            mRecyclerView.setAdapter(mTrailerAdapter);
        } else if (loader.getId() == REVIEW_LOADER) {
            mReviews = parseReviewJson(data);
            reviewText.setText("");
            if (mReviews.size() < 1) {
                reviewText.setText(NO_REVIEWS);
            }
            for (int i = 0; i < mReviews.size(); i++) {
                reviewText.append("Author:  " + mReviews.get(i).getAuthor() + "\n");
                reviewText.append("Content:  " + mReviews.get(i).getContent() + "\n\n");

            }

        }


    }

    private List<TrailerInfo> parseTrailerJson(String jsonString) {

        String baseUrl = "https://www.youtube.com/watch?v=";

        String resultsKey = "results";
        String nameKey = "name";
        String video_Key = "key";

        List<TrailerInfo> mTrailerList = new ArrayList<TrailerInfo>();

        try {
            JSONObject trailer = new JSONObject(jsonString);
            JSONArray results = trailer.getJSONArray(resultsKey);
            if (results != null) {

                for (int i = 0; i < results.length(); i++) {

                    String video = results.getJSONObject(i).getString(video_Key);

                    String name = results.getJSONObject(i).getString(nameKey);

                    mTrailerList.add(new TrailerInfo(name, video));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mTrailerList;
    }

    private List<ReviewInfo> parseReviewJson(String jsonString) {

        String resultsKey = "results";
        String authorKey = "author";
        String contentKey = "content";

        List<ReviewInfo> mReviewList = new ArrayList<ReviewInfo>();

        try {
            JSONObject trailer = new JSONObject(jsonString);
            JSONArray results = trailer.getJSONArray(resultsKey);
            if (results != null) {

                for (int i = 0; i < results.length(); i++) {

                    String author = results.getJSONObject(i).getString(authorKey);

                    String content = results.getJSONObject(i).getString(contentKey);

                    mReviewList.add(new ReviewInfo(author, content));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mReviewList;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        loader.forceLoad();
    }

    @Override
    public void onTrailerClicked(int clickedMovieIndex) {
        String videoKey = mTrailers.get(clickedMovieIndex).key;

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }


    }

    class ConnectionReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (intent.getAction() != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                getJsonResponse(TRAILER_API, TRAILER_LOADER);
                if (isReviewOpen) {
                    getJsonResponse(REVIEW_API, REVIEW_LOADER);
                }

            }
        }
    }
}

