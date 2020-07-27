package com.exe.musicchart;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.exe.domain.Album;
import com.exe.domain.Chart;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchChartDTO;
import com.exe.dto.SearchSingerDTO;
import com.exe.dto.SearchSongDTO;
import com.exe.service.FloCrawlingService;
import com.exe.service.ItunesCrawlingService;
import com.exe.service.ApiServiceImpl;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j
public class RestMusicController {
	
    @Autowired
    ApiServiceImpl apiService;
    
    /*
     * 기능: 차트 정보 반환 
     * 파라미터: date 날짜(8자리 20200628) 
     */
    @GetMapping(value = "/api/getTodayChart/{date}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE})	 
	public List getTodayChart(
			@PathVariable("date") String date
			) {     	
    	return apiService.getTodayChart(date);
	}
    
    @GetMapping(value = "/api/getInfo/{searchWord}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE})	 
	public List getSingerInfo(
			@PathVariable("searchWord") String searchWord
			) {     	
    	
    	return apiService.getSingerInfo(searchWord);
	}    
    
}
