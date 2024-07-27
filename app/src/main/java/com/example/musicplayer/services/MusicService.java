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

    public static final String ACTION_SEND_SONG_NAME = "ACTION_SEND_SONG_NAME";

    public static final String EXTRA_SONG_NAME = "EXTRA_SONG_NAME";

    private MediaPlayer mediaPlayer;
    private ArrayList<String> musicFiles;
    private int currentIndex = 0;
    private boolean isInitialized = false;
    private boolean isPlaying = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent.getAction() != null;

        if (!isInitialized) {
            initializeMediaPlayer();
        }

        switch (intent.getAction()) {
            case ACTION_PLAY:
                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    break;
                }

                mediaPlayer.start();
                isPlaying = true;
                break;


            case ACTION_STOP:
                if (mediaPlayer == null) {
                    break;
                }

                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;

            case ACTION_NEXT:
                playNextMusic();
                break;

            case ACTION_PREVIOUS:
                if (currentIndex == 0) {
                    currentIndex = musicFiles.size() - 1;
                }
                currentIndex = (currentIndex - 1) % musicFiles.size();
                playMusic(musicFiles.get(currentIndex));
                break;
        }
        return flags;
    }

    private void sendSongName(String songPath) {
        String songName = "";

        // get the song name from the path
        if (songPath != null) {
            String[] parts = songPath.split("/");
            songName = parts[parts.length - 1];
        }

        // remove the .mp3 extension
        songName = songName.substring(0, songName.length() - 4);

        Log.d(TAG, "sendSongName: "+ songName);
        Intent brodcastIntent = new Intent(ACTION_SEND_SONG_NAME);
        brodcastIntent.putExtra(EXTRA_SONG_NAME, songName);
        sendBroadcast(brodcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void initializeMediaPlayer() {
        if (isInitialized) {
            return;
        }

        isInitialized = true;
        isPlaying = true;
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
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                sendSongName( filePath); // Send song name after starting the song
            });
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

    /**
     * Play a song by index
     * for recycler view
     *
     * @param index the index of the song to play
     */
    private void playSongByIndex(int index) {
        if (index < 0 || index >= musicFiles.size()) {
            return;
        }
        currentIndex = index;
        playMusic(musicFiles.get(currentIndex));
    }


}