package com.example.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "DDD-MainActivity";

    private MaterialButton main_MB_play;
    private MaterialButton main_MB_stop;
    private MaterialButton main_MB_next;
    private MaterialButton main_MB_previous;
    private MaterialButton main_MB_pause;
    private MaterialButton main_MB_resume;

    private MaterialTextView main_MTV_name;

    private PermissionManager permissionManager;


    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager(this);



        findViews();
        btnActions();
    }


    private void btnActions() {
        main_MB_play.setOnClickListener(v -> {
            if (permissionManager.isReadStoragePermissionGranted(this)) {
                Log.d(TAG, "btnActions: Permission Granted");
                Intent intent = new Intent(this, MusicService.class);
                intent.setAction(MusicService.ACTION_PLAY);
                startService(intent);
            } else {
                Toast.makeText(this, "Please grant read storage permission", Toast.LENGTH_SHORT).show();
            }
        });

        main_MB_stop.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(MusicService.ACTION_STOP);
            startService(intent);
        });

        main_MB_next.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(MusicService.ACTION_NEXT);
            startService(intent);
        });

        main_MB_previous.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(MusicService.ACTION_PREVIOUS);
            startService(intent);
        });

        main_MB_pause.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(MusicService.ACTION_PAUSE);
            startService(intent);
        });

        main_MB_resume.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(MusicService.ACTION_RESUME);
            startService(intent);
        });

    }



    private void findViews() {
        main_MTV_name = findViewById(R.id.main_MTV_name);


        main_MB_play = findViewById(R.id.main_MB_play);
        main_MB_stop = findViewById(R.id.main_MB_stop);
        main_MB_next = findViewById(R.id.main_MB_next);
        main_MB_previous = findViewById(R.id.main_MB_previous);
        main_MB_pause = findViewById(R.id.main_MB_pause);
        main_MB_resume = findViewById(R.id.main_MB_resume);
    }

}