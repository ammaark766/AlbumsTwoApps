package com.example.appb;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appb.ui.AlbumsFragment;

//single activity entry point for appb
//this activing host the AlbumsFragment (contains the UI and logic for displaying and editing albums via appa's content provider)

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity_main.axml contains a layout used to host fragments
        setContentView(R.layout.activity_main);

        //only add the fragment for the first time
        //if the activity is recreated (like during rotation), the fragment manager restores it automatically
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new AlbumsFragment())
                    .commit();
        }
    }
}