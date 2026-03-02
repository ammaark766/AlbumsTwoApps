package com.example.appa.data;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//ROOM entity - one row in the "albums" table
//appa stores albums in this table and then exposes them to other apps throguh ContentProvider
@Entity(tableName = "albums") //room creates a sql tables named albums
public class AlbumEntity {
    // Primary key for each album row
    @PrimaryKey(autoGenerate = true) // = true means Room/SQLite will create a unique ID for each inserted row
    public long id;

    // Columns for album data
    public String title;
    public String artist;


}
