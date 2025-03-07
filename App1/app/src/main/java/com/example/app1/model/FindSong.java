package com.example.app1.model;

import android.annotation.SuppressLint;

import java.time.LocalDateTime;

public class FindSong {
    private String title;
    private String artist;

    @SuppressLint("NewApi")
    public FindSong(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}