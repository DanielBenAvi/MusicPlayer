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
import com.example.musicplayer.components.MusicPlayerComponent.MusicPlayerComponent;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

public class SongDataActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_ARTIST = "EXTRA_ARTIST";
    public static final String EXTRA_DURATION = "EXTRA_DURATION";

    private MaterialTextView ASD_TV_title;
    private MaterialTextView ASD_TV_artist;
    private MaterialTextView ASD_TV_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_data);

        findViews();

        ASD_TV_title.setText(getIntent().getStringExtra(EXTRA_TITLE));
        String artist = getIntent().getStringExtra(EXTRA_ARTIST);
        if (artist == null) {
            artist = "Unknown artist";
        }
        ASD_TV_artist.setText(artist);
        int duration = getIntent().getIntExtra(EXTRA_DURATION, 0);
        ASD_TV_duration.setText(String.format(Locale.US,"%02d:%02d", duration / 60, duration % 60));
    }

    private void findViews() {
        ASD_TV_title = findViewById(R.id.ASD_TV_title);
        ASD_TV_artist = findViewById(R.id.ASD_TV_artist);
        ASD_TV_duration = findViewById(R.id.ASD_TV_duration);
    }
}