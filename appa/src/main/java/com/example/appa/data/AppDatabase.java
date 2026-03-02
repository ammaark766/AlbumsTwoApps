package com.example.appa.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//ROOM database for appa
//app a stores albums locally using room
//appb does ntoa cess this database directly appB talks to appa through content provider
@Database(entities = {AlbumEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    //exposes DAO method like query,insert,update,delete for albums table
    public abstract AlbumDao albumDao();

    //singleton patter instance so the app only creates one DB object
    private static volatile AppDatabase INSTANCE;

    //singelton pattern (so we dnt create musltiple db objects)
    //volatile + synchronized and double check prevents creating multiple instances in multi thread cases
    //getApplicationContext() prevents leaking an Activity context
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {

            //use synchronized below because more than one thread can call getInstance() at the same time especially in Android (providers, activities, background threads)
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "albums_db" //DB file name on the device
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}