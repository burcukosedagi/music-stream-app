package com.example.app1.model;

import android.annotation.SuppressLint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Song {
    private String title;
    private String artist;
    private int categoryId;
    private int duration;
    private String filePath;
    private String imagePath;
    private LocalDateTime updateTime;
    private LocalDateTime addedTime;

    // Constructor
    @SuppressLint("NewApi")
    public Song(String title, String artist, int categoryId, int duration, String filePath, String imagePath) {
        this.title = title;
        this.artist = artist;
        this.categoryId = categoryId;
        this.duration = duration;
        this.filePath = filePath;
        this.imagePath = imagePath;

        this.updateTime = LocalDateTime.now();
        this.addedTime =  LocalDateTime.now();
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFormattedDuration() {
        int minutes = duration / 60;  // Saniyeyi dakikaya çevir
        int seconds = duration % 60;  // Kalan saniyeyi al
        return String.format("%02d:%02d", minutes, seconds);
    }


}
