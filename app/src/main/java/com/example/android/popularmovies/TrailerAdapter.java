package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    final private onTrailerClickedListener mTrailerClickedListener;
    private Context mContext;
    private List<TrailerInfo> trailers;

    public TrailerAdapter(onTrailerClickedListener mTrailerClickedListener, Context mContext) {
        this.mTrailerClickedListener = mTrailerClickedListener;
        this.mContext = mContext;
    }


    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        int layoutIdForTrailer = R.layout.trailer;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForTrailer, parent, false);
        return new TrailerViewHolder(view);

    }

    public void setTrailers(List<TrailerInfo> trailerList) {
        trailers = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        TextView textView = holder.itemView.findViewById(R.id.tv_trailer);
        textView.setText(trailers.get(position).getName());


    }

    @Override
    public int getItemCount() {
        //return 0;
        return trailers.size();
    }

    public interface onTrailerClickedListener {
        void onTrailerClicked(int clickedMovieIndex);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int trailerPosition = getAdapterPosition();
            mTrailerClickedListener.onTrailerClicked(trailerPosition);

        }
    }

}

