package com.example.android.popularmovies;

public class TrailerInfo {

    String name;
    String key;

    public TrailerInfo(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
