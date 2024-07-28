package com.example.musicplayer.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicplayer.R;
import com.google.android.material.textview.MaterialTextView;

public class SongDataActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_ARTIST = "EXTRA_ARTIST";
    public static final String EXTRA_DURATION = "EXTRA_DURATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_data);

        MaterialTextView ASD_TV_title = findViewById(R.id.ASD_TV_title);
        MaterialTextView ASD_TV_artist = findViewById(R.id.ASD_TV_artist);
        MaterialTextView ASD_TV_duration = findViewById(R.id.ASD_TV_duration);

        ASD_TV_title.setText(getIntent().getStringExtra(EXTRA_TITLE));
        ASD_TV_artist.setText(getIntent().getStringExtra(EXTRA_ARTIST));
        ASD_TV_duration.setText(getIntent().getStringExtra(EXTRA_DURATION));
    }
}