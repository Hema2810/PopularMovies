package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM MovieInfo")
    LiveData<List<MovieInfo>> loadFavourites();

   /* @Query("SELECT * FROM MovieInfo WHERE id LIKE :id")
    MovieInfo findByMovieId(int id);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieInfo movieinfo);

    @Delete
    void deleteMovie(MovieInfo movieinfo);
}
