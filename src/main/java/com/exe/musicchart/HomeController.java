package com.exe.musicchart;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.exe.service.FloCrawlingService;
import com.exe.service.ItunesCrawlingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

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
