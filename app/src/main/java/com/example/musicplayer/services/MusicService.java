package com.example.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    public static final String TAG = "DDD-MusicServer";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";


    private MediaPlayer mediaPlayer;
    private ArrayList<String> musicFiles;
    private int currentIndex = 0;

    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent.getAction() != null;

        switch (intent.getAction()) {
            case ACTION_PLAY:
                initializeMediaPlayer();
                break;
            case ACTION_STOP:
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                break;
            case ACTION_NEXT:
                playNextMusic();
                break;
            case ACTION_PREVIOUS:
                currentIndex = (currentIndex - 1) % musicFiles.size();
                playMusic(musicFiles.get(currentIndex));
                break;
            case ACTION_PAUSE:
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                break;
            case ACTION_RESUME:
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                break;
        }
        return flags;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void initializeMediaPlayer() {
        File musicDir = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                : new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music");

        musicFiles = getMusicFilesFromDirectory(musicDir);

        if (!musicFiles.isEmpty()) {
            playMusic(musicFiles.get(currentIndex));
        } else {
            Toast.makeText(this, "No music files found", Toast.LENGTH_SHORT).show();
        }
    }

    private void playMusic(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(filePath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> playNextMusic());
        } catch (IOException e) {
            Log.d(TAG, "playMusic: "+ e.getMessage());
            Toast.makeText(this, "Error playing file: " + filePath, Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextMusic() {
        currentIndex = (currentIndex + 1) % musicFiles.size();
        playMusic(musicFiles.get(currentIndex));
    }

    private ArrayList<String> getMusicFilesFromDirectory(File directory) {
        ArrayList<String> fileList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    fileList.add(file.getAbsolutePath());
                }
            }
        }
        return fileList;
    }
}