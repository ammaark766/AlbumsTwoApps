package com.example.appa.provider;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appa.data.AlbumEntity;
import com.example.appa.data.AppDatabase;

//appa contentprovider
//exposes albums (titles + artist) to other apps via a contentResolver
//Support query/insert/update/delete through URIs
//Notify other apps when albums change (notifyChange) so observers refresh (App B)
public class AlbumsProvider extends ContentProvider {

    //URI matcher codes (to help us decide which op the caller wants)
    private static final int ALBUMS = 1; // content://authority/albums
    private static final int ALBUM_ID = 2;  // content://authority/albums/<id>

    //URImatcher maps the incoming URIS to the integer codes above
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        //collection endpoin /albums
        uriMatcher.addURI(AlbumsContract.AUTHORITY, AlbumsContract.PATH_ALBUMS, ALBUMS);

        //single item endpoint: /albums/# (# means a numeric ID part of the URI)
        uriMatcher.addURI(AlbumsContract.AUTHORITY, AlbumsContract.PATH_ALBUMS + "/#", ALBUM_ID);
    }


    //instance of ROOM database (appa owns the DB)
    private AppDatabase db;

    @Override
    public boolean onCreate() {
        //initializes ROOM DB here so the provider can serve the requests immediately.
        db = AppDatabase.getInstance(getContext());
        return true;
    }


    //Handles ContentResolver.query()
    //App B uses this to request albums
    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder
    ) {
        int match = uriMatcher.match(uri);

        Cursor cursor;
        if (match == ALBUMS) {
            // Return all albums as a Cursor (Room DAO file provides Cursor methods)
            cursor = db.albumDao().getAllAsCursor();
        } else if (match == ALBUM_ID) {
            // Return a single album by id based on the last URI segment
            long id = ContentUris.parseId(uri);
            cursor = db.albumDao().getByIdAsCursor(id);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Required for ContentObserver to work:
        // App B registers an observer on the albums URI.By setting the notification URI on the Cursor, Android then knows which URI this Cursor is associated with.
        cursor.setNotificationUri(getContext().getContentResolver(), AlbumsContract.CONTENT_URI);
        return cursor;
    }


    //Handles ContentResolver.insert()
    //App B uses this to add new albums to appa's Room DB
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //Insert here only supported on the collection URI (/albums)
        if (uriMatcher.match(uri) != ALBUMS) {
            throw new IllegalArgumentException("Insert not supported for: " + uri);
        }

        //converts the content values into ROOMS entitys
        AlbumEntity album = new AlbumEntity();
        album.title = values.getAsString(AlbumsContract.COL_TITLE);
        album.artist = values.getAsString(AlbumsContract.COL_ARTIST);

        //save into ROOM and get the next row ID
        long id = db.albumDao().insert(album);

        // Notify observers (App B) that the album's data changed
        //only key piece that triggers appb's ContentObserver.
        getContext().getContentResolver().notifyChange(AlbumsContract.CONTENT_URI, null);

        // this returns the URI for the newly created row: /albums/<id>
        return ContentUris.withAppendedId(AlbumsContract.CONTENT_URI, id);
    }


    //Handles ContentResolver.update().
    //Update requires an item URI (/albums/<id>).
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match != ALBUM_ID) {
            throw new IllegalArgumentException("Update requires /albums/# URI: " + uri);
        }

        long id = ContentUris.parseId(uri);

        // Pull updated fields out of ContentValues
        String title = values.getAsString(AlbumsContract.COL_TITLE);
        String artist = values.getAsString(AlbumsContract.COL_ARTIST);

        // Update in Room
        int rows = db.albumDao().updateById(id, title, artist);

        // Only notify if something actually changed
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(AlbumsContract.CONTENT_URI, null);
        }
        return rows;
    }

    //Handles ContentResolver.delete().
    //Delete requires an item URI (/albums/<id>).
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match != ALBUM_ID) {
            throw new IllegalArgumentException("Delete requires /albums/# URI: " + uri);
        }

        long id = ContentUris.parseId(uri);

        // Delete in Room
        int rows = db.albumDao().deleteById(id);

        // Notify observers (App B) so UI refreshes
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(AlbumsContract.CONTENT_URI, null);
        }
        return rows;
    }


    //Mime type hints // NOTE this is good practice
    //this below is used by other apps to understand whether the URI returns a list or a single item
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == ALBUMS) {
            return "vnd.android.cursor.dir/vnd." + AlbumsContract.AUTHORITY + ".albums";
        } else if (match == ALBUM_ID) {
            return "vnd.android.cursor.item/vnd." + AlbumsContract.AUTHORITY + ".albums";
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
}