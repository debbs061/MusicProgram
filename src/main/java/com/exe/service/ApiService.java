package com.exe.service;

import java.util.List;
import java.util.Map;

import com.exe.domain.Album;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchChartDTO;
import com.exe.dto.SearchSingerDTO;
import com.exe.dto.SearchSongDTO;

public interface ApiService {
	
	public List getTodayChart(String date);
	public List getSingerInfo(String searchWord);
}
