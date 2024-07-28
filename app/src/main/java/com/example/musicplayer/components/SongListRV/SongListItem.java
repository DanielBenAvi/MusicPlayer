package com.example.musicplayer.components.SongListRV;

public class SongListItem {
    String songName;

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
}
