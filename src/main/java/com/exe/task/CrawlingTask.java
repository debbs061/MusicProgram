package com.exe.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.exe.dao.AlbumDAO;
import com.exe.dao.ChartDAO;
import com.exe.dao.SingerDAO;
import com.exe.dao.SongDAO;
import com.exe.service.FloCrawlingService;
import com.exe.service.ItunesCrawlingService;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class CrawlingTask {
	
	@Autowired
	ItunesCrawlingService ics;
	
	@Autowired
	FloCrawlingService fcs; 
	
	/*
	 * 매일 새벽 2시마다 각 사이트 크롤링 한다.
	 */
	@Scheduled(cron="0 56 3 * * *")
	public void checkFiles()throws Exception{
	System.out.println("작동 시작합니다 ");
		ics.ItunesChartCrawling();
		fcs.FloChartCrawling();
	}

}
