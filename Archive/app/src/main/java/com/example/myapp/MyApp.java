package com.example.myapp;

import android.app.Application;
import android.util.Log;

import io.paperdb.Paper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform initialization or setup tasks here
        Paper.init(this);

    }
}
