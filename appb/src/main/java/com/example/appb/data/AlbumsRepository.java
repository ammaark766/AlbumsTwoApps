package com.example.appb.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.appb.model.Album;
import com.example.appb.provider.AlbumsContract;

import java.util.ArrayList;
import java.util.List;


// repository for appb
//this class is the data layer which talks to app a throught the contentreolver
//app b never accesses appa's room database directly it only uses query, insert,update,delete on appa's contentprovider URIS
public class AlbumsRepository {

    //contentresolver is android api for communicating with contentproviders
    private final ContentResolver resolver;

    public AlbumsRepository(ContentResolver resolver) {
        this.resolver = resolver;
    }


    //reads all albums rom appa
    //returns a list<album> for the UI layer to then display in recyclerview
    public List<Album> fetchAll() {
        List<Album> list = new ArrayList<>();

        //querys the collection URI:  content://<authority>/albums
        Cursor cursor = resolver.query(
                AlbumsContract.CONTENT_URI,
                null,   // projection = null means all columns
                null, // selection (WHERE) = null means "no filtering"
                null, //selectionArgs = null
                null  // sortOrder = null (provider's default ordering)
        );

            if (cursor == null) return list;
        try {
            // maps column names  to column indexes (safe: throws if provider doesn't return a column)
            int idIdx = cursor.getColumnIndexOrThrow(AlbumsContract.COL_ID);
            int titleIdx = cursor.getColumnIndexOrThrow(AlbumsContract.COL_TITLE);
            int artistIdx = cursor.getColumnIndexOrThrow(AlbumsContract.COL_ARTIST);

            //converts each cursor row into an album model objects
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIdx);
                String title = cursor.getString(titleIdx);
                String artist = cursor.getString(artistIdx);
                list.add(new Album(id, title, artist));
            }
        }finally {
            //close toa void memory leaks
            cursor.close();

        }
        return list;
    }


    //adds a new album to appa by inserting into the collections URI
    public void add(String title, String artist) {
        ContentValues values = new ContentValues();
        values.put(AlbumsContract.COL_TITLE, title);
        values.put(AlbumsContract.COL_ARTIST, artist);
        resolver.insert(AlbumsContract.CONTENT_URI, values);
    }

    //updates an existing album by using the "item" URI: content://<authority>/albums/<id>

    public void update(long id, String title, String artist) {
        ContentValues values = new ContentValues();
        values.put(AlbumsContract.COL_TITLE, title);
        values.put(AlbumsContract.COL_ARTIST, artist);

        Uri itemUri = ContentUris.withAppendedId(AlbumsContract.CONTENT_URI, id);
        resolver.update(itemUri, values, null, null);
    }

    //deletes an album using the "item" URI: content://<authority>/albums/<id>
    public void delete(long id) {
        Uri itemUri = ContentUris.withAppendedId(AlbumsContract.CONTENT_URI, id);
        resolver.delete(itemUri, null, null);
    }
}