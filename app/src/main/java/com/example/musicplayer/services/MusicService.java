package com.example.musicplayer.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.activities.MainActivity;
import com.example.musicplayer.components.SongListRV.Song;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    public static final String TAG = "DDD-MusicServer";

    // Actions
    public static final String ACTION_INIT_MEDIA_PLAYER = "ACTION_INIT_PLAYER";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_SET_CLICKED_SONG = "ACTION_SET_CLICKED_SONG";

    // Broadcasts
    public static final String BROADCAST_SEND_SONG_INDEX = "BROADCAST_SEND_SONG_INDEX";
    public static final String BROADCAST_SEND_SONG_DATA = "BROADCAST_SEND_SONG_DATA";
    public static final String BROADCAST_SEND_All_SONGS_DATA_LIST = "BROADCAST_SEND_All_SONGS_DATA_LIST";

    // Extras
    public static final String EXTRA_SONG_INDEX = "EXTRA_SONG_INDEX";
    public static final String EXTRA_SONG_DATA = "EXTRA_SONG_DATA";
    public static final String EXTRA_ALL_SONGS_DATA_LIST = "EXTRA_ALL_SONGS_DATA_LIST";

    // Notification
    private static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static int lastShownNotificationId = -1;

    private MediaPlayer mediaPlayer;
    private final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    public NotificationCompat.Builder notificationBuilder;
    private ArrayList<String> musicFiles;
    private int currentIndex = 0;

    // Flags
    private boolean isInitialized = false;
    private boolean isPlaying = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent.getAction() != null;

        switch (intent.getAction()) {
            case ACTION_INIT_MEDIA_PLAYER:
                initializeMediaPlayer();
                break;

            case ACTION_PLAY:
                if (!isInitialized) {
                    Log.d(TAG, "Please wait for the music player to initialize");
                    break;
                }

                if (mediaPlayer == null) {
                    Log.d(TAG, "Media player is null");
                    initializeMediaPlayer();
                    sendSongsListToActivity();
                }


                if (musicFiles.isEmpty()) {
                    Log.d(TAG, "No music files found");
                    getMusicFilesFromDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
                    sendSongsListToActivity();
                }

                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    playMusic(musicFiles.get(currentIndex));
                    isPlaying = true;
//                    updateNotification(pathToSongName(musicFiles.get(currentIndex)));
                }

                break;

            case ACTION_STOP:
                if (isPlaying) {
                    break;
                }

                if (mediaPlayer == null) {
                    Log.d(TAG, "Media player is null");
                    break;
                }

                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;

            case ACTION_NEXT:
                if (mediaPlayer == null) {
                    Log.d(TAG, "Media player is null");
                    initializeMediaPlayer();
                    sendSongsListToActivity();
                }


                if (musicFiles.isEmpty()) {
                    Log.d(TAG, "No music files found");
                    getMusicFilesFromDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
                    sendSongsListToActivity();
                }

                playNextMusic();
                break;

            case ACTION_PREVIOUS:
                if (mediaPlayer == null) {
                    Log.d(TAG, "Media player is null");
                    initializeMediaPlayer();
                    sendSongsListToActivity();
                }

                if (musicFiles.isEmpty()) {
                    Log.d(TAG, "No music files found");
                    getMusicFilesFromDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
                    sendSongsListToActivity();
                }

                if (currentIndex == 0) {
                    currentIndex = musicFiles.size() - 1;
                }
                currentIndex = (currentIndex - 1) % musicFiles.size();
                playMusic(musicFiles.get(currentIndex));
                break;

            case ACTION_SET_CLICKED_SONG:
                if (!isInitialized) {
                    Toast.makeText(this, "Please wait for the music player to initialize", Toast.LENGTH_SHORT).show();
                    break;
                }

                int index = intent.getIntExtra(EXTRA_SONG_INDEX, 0);
                assert musicFiles != null;
                playSongByIndex(index);
                break;

            default:
                break;
        }

        notifyToUserForForegroundService();

        return flags;
    }


    private void sendSongDataToActivity(String songPath) {
        Song songListItem = getSongData(songPath);
        String songListItemGson = new Gson().toJson(songListItem);
        Intent songListItemIntent = new Intent(BROADCAST_SEND_SONG_DATA);
        songListItemIntent.putExtra(EXTRA_SONG_DATA, songListItemGson);
        sendBroadcast(songListItemIntent);
    }


    private void sendSongsListToActivity() {
        Log.d(TAG, "Sending songs list to activity");
        ArrayList<Song> songListItems = new ArrayList<>();
        for (String songPath : musicFiles) {
            songListItems.add(getSongData(songPath));
        }

        Log.d(TAG, "Songs list: " + songListItems);

        String songListItemsGson = new Gson().toJson(songListItems);
        Intent songListItemsIntent = new Intent(BROADCAST_SEND_All_SONGS_DATA_LIST);
        songListItemsIntent.putExtra(EXTRA_ALL_SONGS_DATA_LIST, songListItemsGson);
        sendBroadcast(songListItemsIntent);
    }

    private void sendSongIndex(int index) {
        Intent brodcastIntent = new Intent(BROADCAST_SEND_SONG_INDEX);
        brodcastIntent.putExtra(EXTRA_SONG_INDEX, index);
        sendBroadcast(brodcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeMediaPlayer() {
        Log.d(TAG, "Initializing media player");
        if (isInitialized) {
            Log.d(TAG, "Media player is already initialized");
            return;
        }

        if (mediaPlayer != null) {
            Log.d(TAG, "Media player is not null");
            mediaPlayer.release();
        }

        File musicDir = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                : new File(Environment.getExternalStorageDirectory() + "/Music");

        musicFiles = getMusicFilesFromDirectory(musicDir);

        isInitialized = true;
        sendSongsListToActivity();
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
                sendSongDataToActivity(filePath); // Send song name after starting the song
                sendSongIndex(currentIndex); // Send song index after starting the song
            });

            mediaPlayer.setOnCompletionListener(mp -> playNextMusic());
        } catch (IOException e) {
            Log.e(TAG, "Error playing music: " + e.getMessage());
        }
    }

    private void playNextMusic() {
        currentIndex = (currentIndex + 1) % musicFiles.size();
        playMusic(musicFiles.get(currentIndex));
    }

    private ArrayList<String> getMusicFilesFromDirectory(File directory) {
        Log.d(TAG, "Getting music files from directory: " + directory.getAbsolutePath());
        ArrayList<String> fileList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    fileList.add(file.getAbsolutePath());
                }
            }
        }
        musicFiles = fileList;
        Log.d(TAG, "Found " + fileList.size() + " music files");
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

    public static String pathToSongName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4);
    }

    public Song getSongData(String songPath) {
        mediaMetadataRetriever.setDataSource(songPath);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int durationInSeconds = Integer.parseInt(duration) / 1000;
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        return new Song()
                .setSongName(pathToSongName(songPath))
                .setSongDuration(durationInSeconds)
                .setSongArtist(artist);
    }


    // // // // // // // // // // // // // // // // Notification  // // // // // // // // // // // // // // //

    private void notifyToUserForForegroundService() {
        // Check if the notification permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notification permission is not granted");
            return;
        }



        // On notification click
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(NOTIFICATION_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextButtonIntent = new Intent(this, MusicService.class);
        nextButtonIntent.setAction(ACTION_NEXT);
        PendingIntent nextButtonPendingIntent = PendingIntent.getService(this, 0, nextButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = getNotificationBuilder(this,
                CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top


        if (mediaPlayer == null) {
            initializeMediaPlayer();
            return;
        }

        if (musicFiles.isEmpty()) {
            return;
        }

        notificationBuilder
                .setContentIntent(pendingIntent) // Open activity
                .setOngoing(true)
                .setSmallIcon(R.drawable.play_arrow_24px)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle("Music Player")
                .setContentText(pathToSongName(musicFiles.get(currentIndex)))
                .addAction(R.drawable.skip_previous_24px, "Previous", PendingIntent.getService(this, 0, new Intent(this, MusicService.class).setAction(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .addAction(isPlaying ? R.drawable.pause_24px : R.drawable.play_arrow_24px, isPlaying ? "Pause" : "Play", PendingIntent.getService(this, 0, new Intent(this, MusicService.class).setAction(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .addAction(R.drawable.skip_next_24px, "Next", nextButtonPendingIntent);

        Notification notification = notificationBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }

        if (NOTIFICATION_ID != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = NOTIFICATION_ID;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        prepareChannel(context, channelId, importance);
        builder = new NotificationCompat.Builder(context, channelId);
        return builder;
    }

    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String notifications_channel_description = "Cycling app location channel";
        final NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(notifications_channel_description);

                // from another answer
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.BLUE);

                nm.createNotificationChannel(nChannel);
            }
        }
    }

    private void updateNotification(String content) {
        notificationBuilder.setContentText(content);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}