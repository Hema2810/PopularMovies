package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private LiveData<List<MovieInfo>> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase mDb = MovieDatabase.getInstance(this.getApplication());
        movies = mDb.movieDao().loadFavourites();
    }

    public LiveData<List<MovieInfo>> getMovies() {
        return movies;
    }
}
