package com.example.musicplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.components.MusicPlayerComponent.MusicPlayerComponent;
import com.example.musicplayer.components.MusicPlayerComponent.MusicPlayerComponentListener;
import com.example.musicplayer.components.SongListRV.MusicAdapter;
import com.example.musicplayer.components.SongListRV.OnItemClickListener;
import com.example.musicplayer.components.SongListRV.SongListItem;
import com.example.musicplayer.services.MusicService;
import com.example.musicplayer.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayerComponentListener, OnItemClickListener {

    private static final String TAG = "DDD-MainActivity";

    private MusicPlayerComponent main_MPC_music_player;

    private RecyclerView main_RV_songs;
    private ArrayList<SongListItem> songList;
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupRecyclerView();

        IntentFilter intentFilter = new IntentFilter(MusicService.ACTION_SEND_SONG_NAME);
        intentFilter.addAction(MusicService.ACTION_SEND_SONG_NAME);

        intentFilter.addAction(MusicService.GET_LIST_OF_SONGS);



        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);
    }


    private void findViews() {
        main_MPC_music_player = findViewById(R.id.main_MPC_music_player);
        main_MPC_music_player.setSongTitle("Nothing is playing");
        main_MPC_music_player.setListener(this); // set the listener for the MusicPlayerComponent


    }

    private void setupRecyclerView() {
        main_RV_songs = findViewById(R.id.main_RV_songs);
        main_RV_songs.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        musicAdapter = new MusicAdapter(songList, this);
        main_RV_songs.setAdapter(musicAdapter);


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

            switch (action) {
                case MusicService.ACTION_SEND_SONG_NAME:
                    String songName = intent.getStringExtra(MusicService.EXTRA_SONG_NAME);
                    Log.d(TAG, "onReceive: songName: " + songName);
                    main_MPC_music_player.setSongTitle(songName);
                    break;
                case MusicService.GET_LIST_OF_SONGS:
                    ArrayList<String> songsList = intent.getStringArrayListExtra(MusicService.EXTRA_SONGS_LIST);
                    Log.d(TAG, "onReceive: songsList: " + songsList);
                    songList.clear();
                    assert songsList != null;
                    for (String song : songsList) {
                        songList.add(new SongListItem(MusicService.pathToSongName(song)));
                    }
                    musicAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onReceive: songsList: " + songsList);
                    break;
                default:
                    Log.d(TAG, "onReceive: default");
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: unregisterReceiver");
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void onItemClick(SongListItem songListItem, int position) {
        Log.d(TAG, "onItemClick: songListItem: " + songListItem.getSongName());
        // play the song that was clicked
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_SET_CLICKED_SONG);
        intent.putExtra("index", position);
        startService(intent);
    }
}