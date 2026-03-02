package com.example.appb.provider;

import android.net.Uri;


//contact class shared by appb for talking to appa's contentprovider
public final class AlbumsContract {

    //precents instantiation
    private AlbumsContract() {}

    //must match the authority declared by app a in its manifest file in <provider authorities>
    public static final String AUTHORITY = "com.example.albumstwoapps.provider";

    //base content URI content://<authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //path segment for the albums
    public static final String PATH_ALBUMS = "albums";

    //full collection URI content://<authority>/albums
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALBUMS);


    //column names returned by appa's provider cursor
    //these must also match the column names appa exposes in its qurry() results
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_ARTIST = "artist";
}