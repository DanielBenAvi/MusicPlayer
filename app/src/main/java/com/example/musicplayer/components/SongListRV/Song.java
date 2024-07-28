package com.example.musicplayer.components.SongListRV;

import androidx.annotation.NonNull;

public class Song {
    String songName;
    int songDuration;
    String songArtist;

    public Song(String songName) {
        this.songName = songName;
    }

    public Song() {
    }

    public String getSongName() {
        return songName;
    }

    public Song setSongName(String songName) {
        this.songName = songName;
        return this;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public Song setSongDuration(int songDuration) {
        this.songDuration = songDuration;
        return this;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public Song setSongArtist(String songArtist) {
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
