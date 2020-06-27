package com.exe.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exe.dao.AlbumDAO;
import com.exe.dao.ChartDAO;
import com.exe.dao.SingerDAO;
import com.exe.dao.SongDAO;
import com.exe.domain.Album;
import com.exe.domain.Chart;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchChartDTO;
import com.exe.dto.SearchSingerDTO;
import com.exe.dto.SearchSongDTO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ApiServiceImpl implements ApiService {
	
	@Autowired
	private SqlSessionTemplate sessionTemplate;
	
	/*
	 * ��Ʈ ������ ��ȸ
	 */
	@Override
	public List<Chart> getTodayChart(SearchChartDTO chart) {
		List<Chart> chartList = sessionTemplate.selectList("sourcemapper.getTodayChart", chart);
		return chartList;
	}
	
	/*
	 * ���� ���� ��ȸ
	 */
	@Override
	public Singer getSingerInfo(SearchSingerDTO singer) {
		Singer singerInfo = sessionTemplate.selectOne("sourcemapper.getSingerInfo", singer);
		return singerInfo;
	}
	
	/* 
	 * �� ���� ��ȸ
	 */
	@Override
	public Song getSongInfo(SearchSongDTO song) {
		Song songInfo = sessionTemplate.selectOne("sourcemapper.getSongInfo", song);
		return songInfo;
	}
	
	/*
	 * �ٹ� ���� ��ȸ
	 */
	@Override
	public Album getAlbumInfo(SearchAlbumDTO album) {
		Album albumInfo = sessionTemplate.selectOne("sourcemapper.getAlbumInfo", album);
		log.info(albumInfo);
		return albumInfo;
	}

	
	

}
