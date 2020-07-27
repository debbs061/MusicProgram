package com.exe.musicchart;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.exe.service.FloCrawlingService;
import com.exe.service.ItunesCrawlingService;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
public class MusicController {
		
	@Autowired
	FloCrawlingService floCrawlingService;
	
	@Autowired
	ItunesCrawlingService iTunesCrawlingService;
		
	    
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
