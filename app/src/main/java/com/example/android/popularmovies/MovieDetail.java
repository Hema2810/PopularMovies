package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity {


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

    MovieDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mDb = MovieDatabase.getInstance(getApplicationContext());

        ButterKnife.bind(this);
        String pattern = "yyyy-MM-dd";
        String outPattern = "MMM dd yyyy";

        Intent parentActivityIntent = getIntent();
        if (parentActivityIntent != null) {
            if (parentActivityIntent.getExtras() != null) {
                mMovieInfo = parentActivityIntent.getParcelableExtra(Intent.EXTRA_TEXT);
            }

        }

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

        final int currentId = mMovieInfo.getId();
        final MovieInfo currentMovie;

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
        /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final MovieInfo currentMovie = mDb.movieDao().findByMovieId(currentId);

                if ( currentMovie != null) {
                    mMovieInfo.setFavorite(1);
                      resource = android.R.drawable.btn_star_big_on;
                }
                else {
                    mMovieInfo.setFavorite(0);
                     resource = android.R.drawable.btn_star_big_off;
                }
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favoriteButton.setImageResource(resource);
                        }});}


        });*/

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
}

