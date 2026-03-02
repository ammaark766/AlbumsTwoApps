package com.example.appa.provider;

import android.net.Uri;

//contract class for appa's contentprovider
//defines the provider authority and URIs in once place
//also defines the column names that app a exposes in its cursor
// (NOTE) appb must use the same authority and path when building its contentprovider
public final class AlbumsContract {

    // Prevent instantiation (constants-only class)
    private AlbumsContract() {}

    // Must match <provider android:authorities="..."> in App A AndroidManifest.xml
    public static final String AUTHORITY = "com.example.albumstwoapps.provider";

    // Base URI: content://<authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Collection path segment
    public static final String PATH_ALBUMS = "albums";

    // Collection URI: content://<authority>/albums
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALBUMS);

    // Column names that the provider returns in query() Cursor
    // These should also must match the actual database columns (Room entity fields/column names)
    public static final String COL_TITLE = "title";
    public static final String COL_ARTIST = "artist";
}