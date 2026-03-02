package com.example.appb;

import static org.junit.Assert.*;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.appb.provider.AlbumsContract;

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
    public void appA_provider_isReachable() {
        // If appa isn't installed or authority is wrong this will throw
        Cursor cursor = resolver().query(AlbumsContract.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        cursor.close();
    }

    @Test
    public void insert_intoAppA_then_queryBack_works() {
        ContentResolver cr = resolver();

        // INSERT into App A via ContentResolver (from appb)
        ContentValues values = new ContentValues();
        values.put(AlbumsContract.COL_TITLE, "B Insert Title");
        values.put(AlbumsContract.COL_ARTIST, "B Insert Artist");

        Uri newUri = cr.insert(AlbumsContract.CONTENT_URI, values);
        assertNotNull(newUri);

        long id = ContentUris.parseId(newUri);
        assertTrue(id > 0);

        // QUERY back the inserted row
        Uri itemUri = ContentUris.withAppendedId(AlbumsContract.CONTENT_URI, id);
        Cursor c = cr.query(itemUri, null, null, null, null);
        assertNotNull(c);

        try {
            assertTrue(c.moveToFirst());
            String title = c.getString(c.getColumnIndexOrThrow(AlbumsContract.COL_TITLE));
            String artist = c.getString(c.getColumnIndexOrThrow(AlbumsContract.COL_ARTIST));
            assertEquals("B Insert Title", title);
            assertEquals("B Insert Artist", artist);
        } finally {
            c.close();
        }

        // Cleanup (nowdelete the test data)
        int rows = cr.delete(itemUri, null, null);
        assertEquals(1, rows);
    }
}