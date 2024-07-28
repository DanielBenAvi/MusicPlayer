package com.example.musicplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupMusicService();
        setupRecyclerView();
    }


    private void findViews() {
        main_MPC_music_player = findViewById(R.id.main_MPC_music_player);
        main_RV_songs = findViewById(R.id.main_RV_songs);
    }

    private void setupMusicService() {
        main_MPC_music_player.setSongTitle("Nothing is playing");
        main_MPC_music_player.setListener(this); // set the listener for the MusicPlayerComponent

        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_INIT_PLAYER);
        startService(intent);

        IntentFilter intentFilter = new IntentFilter(MusicService.BROADCAST_SEND_SONG_NAME);
        intentFilter.addAction(MusicService.BROADCAST_SEND_SONG_NAME);
        intentFilter.addAction(MusicService.BROADCAST_GET_LIST_OF_SONGS);
        intentFilter.addAction(MusicService.BROADCAST_SEND_SONG_INDEX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);
        } else {
            // TODO: fix this
            Toast.makeText(this, "SDK version is too low", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupRecyclerView() {
        main_RV_songs.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        musicAdapter = new MusicAdapter(songList, this);
        main_RV_songs.setAdapter(musicAdapter);
    }

    @Override
    public void onPlayPauseClicked() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);
        startService(intent);
    }

    @Override
    public void onNextClicked() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_NEXT);
        startService(intent);
    }

    @Override
    public void onPreviousClicked() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PREVIOUS);
        startService(intent);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            assert action != null;

            switch (action) {
                case MusicService.BROADCAST_SEND_SONG_NAME:
                    String songName = intent.getStringExtra(MusicService.EXTRA_SONG_NAME);
                    main_MPC_music_player.setSongTitle(songName);
                    break;
                case MusicService.BROADCAST_GET_LIST_OF_SONGS:
                    ArrayList<String> songsList = intent.getStringArrayListExtra(MusicService.EXTRA_SONGS_LIST);
                    songList.clear();
                    assert songsList != null;
                    for (String song : songsList) {
                        songList.add(new SongListItem(MusicService.pathToSongName(song)));
                    }
                    musicAdapter.notifyDataSetChanged();
                    break;
                case MusicService.BROADCAST_SEND_SONG_INDEX:
                    int index = intent.getIntExtra(MusicService.EXTRA_SONG_INDEX, 0);
                    musicAdapter.setSelectedIndex(index);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onItemClick(SongListItem songListItem, int position) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_SET_CLICKED_SONG);
        intent.putExtra(MusicService.EXTRA_SONG_INDEX, position);
        startService(intent);
    }
}