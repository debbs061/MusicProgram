package com.exe.dto;

public class SearchAlbumDTO {
    private String singerKey;
    private String albumTitle;

    public String getSingerKey(){return singerKey;}
    public void setSingerKey(String singerKey){this.singerKey = singerKey;}
    public String  getAlbumTitle(){return albumTitle;}
    public void setAlbumTitle(String albumTitle){this.albumTitle = albumTitle;}
}
