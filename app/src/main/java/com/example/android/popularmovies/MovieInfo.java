package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable {


    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
    String mMovieTitle;
    String mThumbnailImage;
    String mReleaseDate;
    String mSynopsis;
    String mRating;


    public MovieInfo(String mMovieTitle, String mThumbnailImage, String mReleaseDate, String mSynopsis, String mRating) {
        this.mMovieTitle = mMovieTitle;
        this.mThumbnailImage = mThumbnailImage;
        this.mReleaseDate = mReleaseDate;
        this.mSynopsis = mSynopsis;
        this.mRating = mRating;
    }

    protected MovieInfo(Parcel in) {
        mMovieTitle = in.readString();
        mThumbnailImage = in.readString();
        mReleaseDate = in.readString();
        mSynopsis = in.readString();
        mRating = in.readString();
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String mMovieTitle) {
        this.mMovieTitle = mMovieTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public String getThumbnailImage() {
        return mThumbnailImage;
    }

    public void setThumbnailImage(String mThumbnailImage) {
        this.mThumbnailImage = mThumbnailImage;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String mRating) {
        this.mRating = mRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieTitle);
        dest.writeString(mThumbnailImage);
        dest.writeString(mReleaseDate);
        dest.writeString(mSynopsis);
        dest.writeString(mRating);
    }
}

