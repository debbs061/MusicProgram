package com.exe.dto;

public class SearchSongDTO {
    private String songTitle;
    private String albumKey;

    public String getSongTitle(){return songTitle;}
    public void setSongTitle(String songTitle){this.songTitle = songTitle;}
    public String getAlbumKey(){return albumKey;}
    public  void setAlbumKey(String albumKey){this.albumKey = albumKey;}
}
