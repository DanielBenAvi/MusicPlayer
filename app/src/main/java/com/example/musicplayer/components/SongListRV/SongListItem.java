package com.example.musicplayer.components.SongListRV;

import androidx.annotation.NonNull;

public class SongListItem {
    String songName;
    int songDuration;
    String songArtist;

    public SongListItem(String songName) {
        this.songName = songName;
    }

    public SongListItem() {
    }

    public String getSongName() {
        return songName;
    }

    public SongListItem setSongName(String songName) {
        this.songName = songName;
        return this;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public SongListItem setSongDuration(int songDuration) {
        this.songDuration = songDuration;
        return this;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public SongListItem setSongArtist(String songArtist) {
        this.songArtist = songArtist;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "SongListItem{" +
                "songName='" + songName + '\'' +
                ", songDuration=" + songDuration +
                ", songArtist='" + songArtist + '\'' +
                '}';
    }
}
