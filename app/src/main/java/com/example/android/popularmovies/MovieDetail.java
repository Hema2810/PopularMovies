package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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

    }
}

