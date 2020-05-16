package com.exe.domain;

public class Album {
    private String albumKey;
    private String albumTitle;
    private String albumImage;
    private String singerKey;

    public String getAlbumKey(){return albumKey;}
    public void setAlbumKey(String albumKey){this.albumKey = albumKey;}
    public String getAlbumTitle(){return albumTitle;}
    public void setAlbumTitle(String albumTitle){this.albumTitle = albumTitle;}
    public String getAlbumImage(){return albumImage;}
    public void setAlbumImage(String albumImage){this.albumImage = albumImage;}
    public String getSingerKey(){return singerKey;}
    public void setSingerKey(String singerKey){this.singerKey = singerKey;}
}
