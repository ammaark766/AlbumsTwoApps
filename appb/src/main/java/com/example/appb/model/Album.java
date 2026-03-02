package com.example.appb.model;

public class Album {


    //prt fields
    private long id;
    private String title;
    private String artist;

    //constructor
    public Album(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}