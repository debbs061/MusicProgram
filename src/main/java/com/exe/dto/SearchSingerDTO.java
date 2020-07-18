package com.exe.dto;

import lombok.Data;

@Data
public class SearchSingerDTO {
	private String singer_key;
	private String singer_name;
	private String debut_date;
	private String agency;
}
