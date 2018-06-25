package com.example.android.popularmovies;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MovieInfo.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private final static Object LOCK = new Object();
    private static final String DATABASE_NAME = "FavouriteMovies";
    private static MovieDatabase sInstance;


    public static MovieDatabase getInstance(Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class,
                        MovieDatabase.DATABASE_NAME)
                        .build();

            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();

}
