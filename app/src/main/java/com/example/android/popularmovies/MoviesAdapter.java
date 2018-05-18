package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private static final float IMAGEVIEW_HEIGHT_RATIO = (float) 1.5;
    private final ArrayList<MovieInfo> mMovieInfo;

    public MoviesAdapter(ArrayList<MovieInfo> movieInfo) {
        this.mMovieInfo = movieInfo;

    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context mContext = parent.getContext();
        int layoutIdForMovie = R.layout.movie_grid;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForMovie, parent, false);
        return new MoviesViewHolder(view);

    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        final ImageView mMovieImageView;

        MoviesViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.iv_movies);
            int gridWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels / MoviesActivity.NUM_GRID_COLUMNS;
            mMovieImageView.setMinimumWidth(gridWidth);
            int gridHeight = (int) (gridWidth * IMAGEVIEW_HEIGHT_RATIO);
            mMovieImageView.setMinimumHeight(gridHeight);

        }
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder holder,  int position) {

        final int ePosition = position;


        Picasso.with(holder.mMovieImageView.getContext())
                .load(mMovieInfo.get(position).mThumbnailImage)
                .placeholder(R.drawable.baseline_image_search_24)
                .error(R.drawable.baseline_image_search_24)
                .into(holder.mMovieImageView);


        holder.mMovieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(holder.itemView.getContext(), MovieDetail.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mMovieInfo.get(ePosition));
                holder.itemView.getContext().startActivity(detailIntent);
                //Toast.makeText(holder.itemView.getContext(), "onClick Listener at " + position, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieInfo.size();
    }


}

