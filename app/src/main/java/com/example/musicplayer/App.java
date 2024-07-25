package com.example.musicplayer;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    public static final String TAG = "App";



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: App");


    }
}
