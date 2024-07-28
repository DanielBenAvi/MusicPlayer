package com.example.musicplayer.components.MusicPlayerComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.musicplayer.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MusicPlayerComponent extends LinearLayout {

    private MaterialTextView mpc_TV_title;

    private MusicPlayerComponentListener listener;

    public MusicPlayerComponent(Context context) {
        super(context);
        init(context);
    }

    public MusicPlayerComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    /**
     * This method is used to set the listener for the MusicPlayerComponent
     * must be called before using the component
     * @param listener the listener to be set MusicPlayerComponentListener
     */
    public void setListener(MusicPlayerComponentListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.music_player_component, this, true);

        mpc_TV_title =  findViewById(R.id.mpc_TV_title);
        ShapeableImageView mpc_IB_play_pause = findViewById(R.id.mpc_IB_play_pause);
        ShapeableImageView mpc_IB_next = findViewById(R.id.mpc_IB_next);
        ShapeableImageView mpc_IB_previous = findViewById(R.id.mpc_IB_previous);

        mpc_IB_play_pause.setOnClickListener(v -> listener.onPlayPauseClicked());

        mpc_IB_next.setOnClickListener(v -> listener.onNextClicked());

        mpc_IB_previous.setOnClickListener(v -> listener.onPreviousClicked());
    }

    public void setSongTitle(String title) {
        mpc_TV_title.setText(title);
    }
}

