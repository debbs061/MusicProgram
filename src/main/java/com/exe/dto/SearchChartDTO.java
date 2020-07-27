package com.exe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SearchChartDTO {
	@JsonIgnore
	private String date;
	@JsonIgnore
	private String site_name;
	private String singer_name;
	private String song_title;
	private String album_title;
	private String album_image;
	private String youtube_link;
	private int rank;
	private int rank_change;
}
