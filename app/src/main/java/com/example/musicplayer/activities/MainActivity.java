package com.example.musicplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.components.MusicPlayerComponent.MusicPlayerComponent;
import com.example.musicplayer.components.MusicPlayerComponent.MusicPlayerComponentListener;
import com.example.musicplayer.services.MusicService;
import com.example.musicplayer.R;

public class MainActivity extends AppCompatActivity implements MusicPlayerComponentListener {

    private static final String TAG = "DDD-MainActivity";

    private MusicPlayerComponent main_MPC_music_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        IntentFilter intentFilter = new IntentFilter(MusicService.ACTION_SEND_SONG_NAME);
        intentFilter.addAction(MusicService.ACTION_SEND_SONG_NAME);

        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);
    }


    private void findViews() {
        main_MPC_music_player = findViewById(R.id.main_MPC_music_player);
        main_MPC_music_player.setSongTitle("Nothing is playing");
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


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            String action = intent.getAction();

            assert action != null;

            assert action.equals(MusicService.ACTION_SEND_SONG_NAME);

            String songName = intent.getStringExtra(MusicService.EXTRA_SONG_NAME);
            Log.d(TAG, "onReceive: songName: " + songName);
            main_MPC_music_player.setSongTitle(songName);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: unregisterReceiver");
        unregisterReceiver(broadcastReceiver);
    }
}