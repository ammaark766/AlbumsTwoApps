package com.example.appa;

import static org.junit.Assert.*;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.appa.provider.AlbumsContract;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private ContentResolver resolver() {
        return InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getContentResolver();
    }

    @Test
    public void provider_isRegistered() {
        // getType() calls into the providef If provider isn't registered, this will throw
        String type = resolver().getType(AlbumsContract.CONTENT_URI);
        // If you removed getType() then type may be null, so just assert no crash
        // assertTrue(true)
        // If you kept getType() assert it's a directory MIME type
        assertNotNull(type);
        assertTrue(type.contains("cursor.dir"));
    }

    @Test
    public void insert_query_update_delete_works() {
        ContentResolver cr = resolver();

        //  INSERT -----
        ContentValues values = new ContentValues();
        values.put(AlbumsContract.COL_TITLE, "Test Title");
        values.put(AlbumsContract.COL_ARTIST, "Test Artist");

        Uri newUri = cr.insert(AlbumsContract.CONTENT_URI, values);
        assertNotNull(newUri);

        long id = ContentUris.parseId(newUri);
        assertTrue(id > 0);

        // QUERY (single item) ----
        Uri itemUri = ContentUris.withAppendedId(AlbumsContract.CONTENT_URI, id);

        Cursor c1 = cr.query(itemUri, null, null, null, null);
        assertNotNull(c1);
        try {
            assertTrue(c1.moveToFirst());
            String title = c1.getString(c1.getColumnIndexOrThrow(AlbumsContract.COL_TITLE));
            String artist = c1.getString(c1.getColumnIndexOrThrow(AlbumsContract.COL_ARTIST));
            assertEquals("Test Title", title);
            assertEquals("Test Artist", artist);
        } finally {
            c1.close();
        }

        //  UPDATE ----
        ContentValues upd = new ContentValues();
        upd.put(AlbumsContract.COL_TITLE, "Updated Title");
        upd.put(AlbumsContract.COL_ARTIST, "Updated Artist");

        int updatedRows = cr.update(itemUri, upd, null, null);
        assertEquals(1, updatedRows);

        Cursor c2 = cr.query(itemUri, null, null, null, null);
        assertNotNull(c2);
        try {
            assertTrue(c2.moveToFirst());
            String title = c2.getString(c2.getColumnIndexOrThrow(AlbumsContract.COL_TITLE));
            String artist = c2.getString(c2.getColumnIndexOrThrow(AlbumsContract.COL_ARTIST));
            assertEquals("Updated Title", title);
            assertEquals("Updated Artist", artist);
        } finally {
            c2.close();
        }

        //  DELETE ----
        int deletedRows = cr.delete(itemUri, null, null);
        assertEquals(1, deletedRows);

        Cursor c3 = cr.query(itemUri, null, null, null, null);
        assertNotNull(c3);
        try {
            assertFalse(c3.moveToFirst()); // should be gone
        } finally {
            c3.close();
        }
    }
}