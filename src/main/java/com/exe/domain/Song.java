package com.exe.domain;

public class Song {
    private String songKey;
    private String songTitle;
    private String relDate;
    private String youtubeLink;
    private String albumKey;

    public String getSongKey(){return songKey;}
    public void setSongKey(String songKey){this.songKey = songKey;}
    public String getSongTitle(){return songTitle;}
    public void setSongTitle(String songTitle){this.songTitle = songTitle;}
    public String getRelDate(){return relDate;}
    public void setRelDate(String relDate){this.relDate = relDate;}
    public String getYoutubeLink(){return youtubeLink;}
    public void setYoutubeLink(String youtubeLink){this.youtubeLink = youtubeLink;}
    public String getAlbumKey(){return albumKey;}
    public void setAlbumKey(String albumKey){this.albumKey = albumKey;}
}
