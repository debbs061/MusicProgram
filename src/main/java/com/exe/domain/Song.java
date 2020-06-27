package com.exe.domain;

import lombok.Data;

@Data
public class Song {
    private String song_key;
    private String song_title;
    private String rel_date;
    private String youtube_link;
    private String album_key;
}
