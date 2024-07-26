package com.example.musicplayer.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayercomponent.MusicPlayerComponent.MusicPlayerComponent;
import com.example.musicplayercomponent.MusicPlayerComponent.MusicPlayerComponentListener;
import com.example.musicplayer.services.MusicService;
import com.example.musicplayer.permissions.PermissionManager;
import com.example.musicplayer.R;

public class MainActivity extends AppCompatActivity implements MusicPlayerComponentListener {

    private static final String TAG = "DDD-MainActivity";

    private MusicPlayerComponent main_MPC_music_player;


    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager permissionManager = new PermissionManager(this);
        findViews();

    }

    private void findViews() {
        main_MPC_music_player = findViewById(R.id.main_MPC_music_player);
        main_MPC_music_player.setSongTitle("Song Title");
        main_MPC_music_player.setListener(this); // set the listener for the MusicPlayerComponent
    }

    @Override
    public void onPlayPauseClicked() {
        Log.d(TAG, "onPlayPauseClicked: ");
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);
        startService(intent);
    }

    @Override
    public void onNextClicked() {
        Log.d(TAG, "onNextClicked: ");
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_NEXT);
        startService(intent);
    }

    @Override
    public void onPreviousClicked() {
        Log.d(TAG, "onPreviousClicked: ");
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PREVIOUS);
        startService(intent);
    }
}