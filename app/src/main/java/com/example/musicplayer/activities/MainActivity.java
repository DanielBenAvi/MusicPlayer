package com.example.musicplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import com.example.musicplayer.components.SongListRV.Song;
import com.example.musicplayer.permissions.PermissionManager;
import com.example.musicplayer.services.MusicService;
import com.example.musicplayer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayerComponentListener, OnItemClickListener {

    private static final String TAG = "DDD-MainActivity";

    private MusicPlayerComponent main_MPC_music_player;
    private RecyclerView main_RV_songs;
    private ArrayList<Song> songList;
    private MusicAdapter musicAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager permissionManager = new PermissionManager(this);

        findViews();
        setupMusicComponent();
        setupMusicService();
        setupRecyclerView();
    }


    private void findViews() {
        main_MPC_music_player = findViewById(R.id.main_MPC_music_player);
        main_RV_songs = findViewById(R.id.main_RV_songs);
    }


    private void setupMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_INIT_MEDIA_PLAYER);
        startService(intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.BROADCAST_SEND_SONG_INDEX);
        intentFilter.addAction(MusicService.BROADCAST_SEND_SONG_DATA);
        intentFilter.addAction(MusicService.BROADCAST_SEND_All_SONGS_DATA_LIST);

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

    @Override
    public void setupMusicComponent() {
        main_MPC_music_player.setSongTitle("Nothing is playing");
        main_MPC_music_player.setListener(this);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            assert action != null;

            switch (action) {

                case MusicService.BROADCAST_SEND_SONG_INDEX:
                    int index = intent.getIntExtra(MusicService.EXTRA_SONG_INDEX, 0);
                    musicAdapter.setSelectedIndex(index);
                    break;
                case MusicService.BROADCAST_SEND_SONG_DATA:
                    String songData = intent.getStringExtra(MusicService.EXTRA_SONG_DATA);
                    Song songListItem = new Gson().fromJson(songData, Song.class);
                    main_MPC_music_player.setSongTitle(songListItem.getSongName());
                    break;

                case MusicService.BROADCAST_SEND_All_SONGS_DATA_LIST:
                    String allSongsData = intent.getStringExtra(MusicService.EXTRA_ALL_SONGS_DATA_LIST);
                    Type listType = new TypeToken<ArrayList<Song>>() {}.getType();
                    ArrayList<Song> songs = new Gson().fromJson(allSongsData, listType);
                    Log.d(TAG, "onReceive: " + songs);
                    songList.clear();
                    songList.addAll(songs);
                    musicAdapter.notifyDataSetChanged();
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
    public void onItemClick(Song songListItem, int position) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_SET_CLICKED_SONG);
        intent.putExtra(MusicService.EXTRA_SONG_INDEX, position);
        startService(intent);


        Intent intent1 = new Intent(this, SongDataActivity.class);
        Log.d(TAG, "onItemClick: " + songListItem.toString());
        intent1.putExtra(SongDataActivity.EXTRA_TITLE, songListItem.getSongName());
        intent1.putExtra(SongDataActivity.EXTRA_ARTIST, songListItem.getSongArtist());
        intent1.putExtra(SongDataActivity.EXTRA_DURATION, songListItem.getSongDuration());
        startActivity(intent1);




    }
}