package com.example.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    public static final String TAG = "DDD-PermissionManager";

    public PermissionManager(Context context) {
        Log.d(TAG, "PermissionManager: ");
        // ask for read storage permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "PermissionManager: Android 10 or above");
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
                Log.d(TAG, "PermissionManager: Requesting Permission for read media audio");
            }
        } else {
            Log.d(TAG, "PermissionManager: Below Android 10");
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                Log.d(TAG, "PermissionManager: Requesting Permission for read external storage");
            }
        }

    }

    public boolean isReadStoragePermissionGranted(Context context) {
        Log.d(TAG, "isReadStoragePermissionGranted: version: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return isReadMediaAudioPermissionGranted(context);
        } else {
            return isReadExternalStoragePermissionGranted(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean isReadMediaAudioPermissionGranted(Context context) {
        boolean granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "isReadMediaAudioPermissionGranted: " + granted);
        return granted;
    }

    public boolean isReadExternalStoragePermissionGranted(Context context) {
        boolean granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "isReadExternalStoragePermissionGranted: " + granted);
        return granted;
    }




}