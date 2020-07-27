package com.exe.dto;

import lombok.Data;

@Data
public class SearchWordDTO {
	private String song_title;
	private String singer_name;
    private String album_title;
    private String youtube_link;
}
