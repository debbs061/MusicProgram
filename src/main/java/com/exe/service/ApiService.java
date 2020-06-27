package com.exe.service;

import java.util.List;

import com.exe.domain.Album;
import com.exe.domain.Chart;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchChartDTO;
import com.exe.dto.SearchSingerDTO;
import com.exe.dto.SearchSongDTO;

public interface ApiService {
	
	public List<Chart> getTodayChart(SearchChartDTO chart);
	public Singer getSingerInfo(SearchSingerDTO singer);
	public Song getSongInfo(SearchSongDTO song);
	public Album getAlbumInfo(SearchAlbumDTO album);
}
