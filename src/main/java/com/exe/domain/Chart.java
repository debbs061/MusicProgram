package com.exe.domain;

import lombok.Data;

@Data
public class Chart {
    private String date;
    private String time;
    private String site_name;
    private int rank;
    private int rank_change;
    private String song_key;
    private String album_key;
    private String singer_key;
}
