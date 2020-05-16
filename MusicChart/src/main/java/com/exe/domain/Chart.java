package com.exe.domain;

public class Chart {
    private String date;
    private String time;
    private String siteName;
    private int rank;
    private int rankChange;
    private String songKey;
    private String albumKey;
    private String singerKey;

    public String getDate(){return date;}
    public void setDate(String date){this.date = date;}
    public String getTime(){return time;}
    public void setTime(String time){this.time = time;}
    public String getSiteName(){return siteName;}
    public void setSiteName(String siteName){this.siteName = siteName;}
    public int getRank(){return rank;}
    public void setRank(int rank){this.rank = rank;}
    public int getrankChange(){return rankChange;}
    public void setRankChange(int rankChange){this.rankChange = rankChange;}
    public String getSongKey(){return songKey;}
    public void setSongKey(String songKey){this.songKey = songKey;}
    public String getAlbumKey(){return albumKey;}
    public void setAlbumKey(String albumKey){this.albumKey = albumKey;}
    public String getSingerKey(){return singerKey;}
    public void setSingerKey(String singerKey){this.singerKey = singerKey;}
}
