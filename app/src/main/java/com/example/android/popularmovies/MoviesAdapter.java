package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private static final float IMAGEVIEW_HEIGHT_RATIO = (float) 1.5;
    final private onMovieClickedListener mMovieClickedListener;
    private ArrayList<MovieInfo> mMovieInfo;
    private Context mContext;

    public MoviesAdapter(Context context, onMovieClickedListener listener) {
        mContext = context;
        mMovieClickedListener = listener;

    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder holder, int position) {

        final int ePosition = position;


        Picasso.with(holder.mMovieImageView.getContext())
                .load(mMovieInfo.get(position).mThumbnailImage)
                .placeholder(R.drawable.baseline_image_search_24)
                .error(R.drawable.baseline_image_search_24)
                .into(holder.mMovieImageView);


    }

    public void setMovies(ArrayList<MovieInfo> movieList) {
        mMovieInfo = movieList;
        notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return mMovieInfo.size();
    }

    public interface onMovieClickedListener {
        void onMovieClicked(int clickedMovieIndex);
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mMovieImageView;

        MoviesViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.iv_movies);
            int gridWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels / MoviesActivity.NUM_GRID_COLUMNS;
            mMovieImageView.setMinimumWidth(gridWidth);
            int gridHeight = (int) (gridWidth * IMAGEVIEW_HEIGHT_RATIO);
            mMovieImageView.setMinimumHeight(gridHeight);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int moviePosition = getAdapterPosition();
            mMovieClickedListener.onMovieClicked(moviePosition);
        }
    }

}

