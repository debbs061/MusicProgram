package com.exe.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	 * 사이트별 차트 데이터 조회
	 */
	@Override
	public List getTodayChart(String date) {
		
		List chartList = new ArrayList();
		
		SearchChartDTO dto = new SearchChartDTO();		
		dto.setSite_name("itunes");
		dto.setDate(date); 
		List itunesChart = sessionTemplate.selectList("sourcemapper.getTodayChart", dto);
		if (!itunesChart.isEmpty()) {	
			Map<String, Object> map = new LinkedHashMap<String, Object>();		
			map.put("site",dto.getSite_name());
			map.put("item",itunesChart);
			chartList.add(map);	
		}	
		
		SearchChartDTO dto2 = new SearchChartDTO();
		dto2.setSite_name("flo");
		dto2.setDate(date);  
		List floChart = sessionTemplate.selectList("sourcemapper.getTodayChart", dto2);
		if (!floChart.isEmpty()) {		
			Map<String, Object> map2 = new LinkedHashMap<String, Object>();		
			map2.put("site",dto2.getSite_name());
			map2.put("item",floChart);
			chartList.add(map2);	
		} 
		
		SearchChartDTO dto3 = new SearchChartDTO();
		dto3.setSite_name("melon");
		dto3.setDate(date);  
		List melonChart = sessionTemplate.selectList("sourcemapper.getTodayChart", dto3);
		if (!melonChart.isEmpty()) {		
			Map<String, Object> map3 = new LinkedHashMap<String, Object>();		
			map3.put("site",dto3.getSite_name());
			map3.put("item",melonChart);
			chartList.add(map3);	
		} 
		
		SearchChartDTO dto4 = new SearchChartDTO();
		dto4.setSite_name("genie");
		dto4.setDate(date);  
		List genieChart = sessionTemplate.selectList("sourcemapper.getTodayChart", dto4);
		if (!genieChart.isEmpty()) {		
			Map<String, Object> map4 = new LinkedHashMap<String, Object>();		
			map4.put("site",dto4.getSite_name());
			map4.put("item",melonChart);
			chartList.add(map4);	
		} 
		
		return chartList;
	}
	
	
	/*
	 * 가수 정보 조회
	 */
	@Override
	public List getSingerInfo(String searchWord) {
		Singer singer = new Singer(); 
		Song song = new Song();
		Album album = new Album();
		
		List searchList = new ArrayList();		
		
		// 가수 검색결과 
		List sList = sessionTemplate.selectList("sourcemapper.getSingerInfo", searchWord);
		Map<String, Object> map = new LinkedHashMap<String, Object>();		
		map.put("type","singer");
		map.put("item",sList);
		searchList.add(map);
		
		// 곡 검색결과 
		List sgList = sessionTemplate.selectList("sourcemapper.getSongInfo", searchWord);
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();
		map2.put("type","song");
		map2.put("item",sgList);
		searchList.add(map2);
		
		// 앨범 검색결과
		List aList = sessionTemplate.selectList("sourcemapper.getAlbumInfo", searchWord);
		Map<String, Object> map3 = new LinkedHashMap<String, Object>();
		map3.put("type","album");
		map3.put("item",aList);
		searchList.add(map3);
				
		return searchList;
	}
	
}
