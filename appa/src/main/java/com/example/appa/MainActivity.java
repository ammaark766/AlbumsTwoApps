package com.example.appa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//appa's role is to host the contentProvider + Room database that other apps appb can  query/insert/update/delete through ContentResolver
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}