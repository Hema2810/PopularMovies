package com.example.android.popularmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
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

    @PrimaryKey
    private int id;
    @Ignore
    private int mFavorite = 0;
    private String mMovieTitle;
    String mThumbnailImage;
    private String mReleaseDate;
    private String mSynopsis;
    private String mRating;


    public MovieInfo(int id, String mMovieTitle, String mThumbnailImage, String mReleaseDate, String mSynopsis, String mRating) {
        this.id = id;
        this.mMovieTitle = mMovieTitle;
        this.mThumbnailImage = mThumbnailImage;
        this.mReleaseDate = mReleaseDate;
        this.mSynopsis = mSynopsis;
        this.mRating = mRating;


    }

    private MovieInfo(Parcel in) {
        id = in.readInt();
        mMovieTitle = in.readString();
        mThumbnailImage = in.readString();
        mReleaseDate = in.readString();
        mSynopsis = in.readString();
        mRating = in.readString();
        mFavorite = in.readInt();
    }

    public int getId() {
        return id;
    }

    public int getFavorite() {
        return mFavorite;
    }

    public void setFavorite(int isFavorite) {
        mFavorite = isFavorite;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

// --Commented out by Inspection START (5/17/18, 9:00 PM):
//    public void setMovieTitle(String mMovieTitle) {
//        this.mMovieTitle = mMovieTitle;
//    }
// --Commented out by Inspection STOP (5/17/18, 9:00 PM)

    public String getReleaseDate() {
        return mReleaseDate;
    }

// --Commented out by Inspection START (5/17/18, 9:00 PM):
//    public void setReleaseDate(String mReleaseDate) {
//        this.mReleaseDate = mReleaseDate;
//    }
// --Commented out by Inspection STOP (5/17/18, 9:00 PM)

    public String getSynopsis() {
        return mSynopsis;
    }

// --Commented out by Inspection START (5/17/18, 9:00 PM):
//    public void setSynopsis(String mSynopsis) {
//        this.mSynopsis = mSynopsis;
//    }
// --Commented out by Inspection STOP (5/17/18, 9:00 PM)

    public String getThumbnailImage() {
        return mThumbnailImage;
    }

// --Commented out by Inspection START (5/17/18, 9:00 PM):
//    public void setThumbnailImage(String mThumbnailImage) {
//        this.mThumbnailImage = mThumbnailImage;
//    }
// --Commented out by Inspection STOP (5/17/18, 9:00 PM)

    public String getRating() {
        return mRating;
    }

// --Commented out by Inspection START (5/17/18, 9:00 PM):
//    public void setRating(String mRating) {
//        this.mRating = mRating;
//    }
// --Commented out by Inspection STOP (5/17/18, 9:00 PM)

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mMovieTitle);
        dest.writeString(mThumbnailImage);
        dest.writeString(mReleaseDate);
        dest.writeString(mSynopsis);
        dest.writeString(mRating);
    }
}

