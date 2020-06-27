package com.exe.musicchart;

import java.io.IOException;
import java.util.List;

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
import com.exe.service.SongServiceImpl;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j
public class HomeController {

    @Autowired
    FloCrawlingService floCrawlingService;
    
    @Autowired
    ItunesCrawlingService iTunesCrawlingService;
    
    @Autowired
    SongServiceImpl songService;
    
    /*
     * 기능: 차트 정보 반환 
     * 파라미터: date 날짜(8자리 20200628) 
     */
    @GetMapping(value = "/api/getTodayChart/{date}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
    				MediaType.APPLICATION_XML_VALUE })	 
	public List<Chart> getTodayChart(
			@PathVariable("date") String date
			) {     	
    	SearchChartDTO dto = new SearchChartDTO();
    	dto.setDate(date);
    	return songService.getTodayChart(dto);
	}
    
    /*
     * 기능: 곡 정보 반환
     */
    @GetMapping(value = "/api/getSongInfo/{songKey}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
    				MediaType.APPLICATION_XML_VALUE })	 
	public Song getSongInfo(
			@PathVariable("songKey") String songKey
			) {     	
    	SearchSongDTO dto = new SearchSongDTO();
    	dto.setSong_key(songKey);
    	return songService.getSongInfo(dto);
	}
    
    /*
     * 기능: 가수 정보 반환
     */
    @GetMapping(value = "/api/getSingerInfo/{singerKey}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
    				MediaType.APPLICATION_XML_VALUE })	 
	public Singer getSingerInfo(
			@PathVariable("singerKey") String singerKey
			) {     	
    	SearchSingerDTO dto = new SearchSingerDTO();
    	dto.setSinger_key(singerKey);
    	return songService.getSingerInfo(dto);
	}
    
    /*
     * 기능: 앨범 정보 반환 
     */
    @GetMapping(value = "/api/getAlbumInfo/{albumKey}", 
    		produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
    				MediaType.APPLICATION_XML_VALUE })	 
	public Album getAlbumInfo(
			@PathVariable("albumKey") String albumKey
			) {     	
    	SearchAlbumDTO dto = new SearchAlbumDTO();
    	dto.setAlbum_key(albumKey);
    	return songService.getAlbumInfo(dto);
	}
            
	@RequestMapping(value = "/", method = {RequestMethod.GET,RequestMethod.POST})
	public String index(HttpServletRequest request) {

		return "index";
	}


	@RequestMapping(value = "/floCrawling", method = {RequestMethod.GET})
    public void getFloCrawling(){
	    floCrawlingService.FloChartCrawling();
    }
	
	
	@RequestMapping(value = "/itunesCrawling", method = {RequestMethod.GET})
    public void getItunesCrawling() throws IOException {
		iTunesCrawlingService.ItunesChartCrawling();
    }	
}
