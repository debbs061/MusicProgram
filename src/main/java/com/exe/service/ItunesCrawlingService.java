package com.exe.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.exe.domain.ChartDate;

@Service
public class ItunesCrawlingService {
	
	public void ItunesChartCrawling() throws IOException {				
		try{
			
			LocalDateTime localDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            String localDate = timestamp.toString();
            String date = localDate.split(" ")[0];
            String time = localDate.split(" ")[1].split("\\.")[0];
            
            ChartDate chartDate = new ChartDate();
            chartDate.setDate(date);
            chartDate.setTime(time);
            chartDate.setSiteName("ITUNES");
			String url = "https://music.apple.com/us/playlist/top-100-global/pl.d25f5d1181894928af76c85c967f8f31";
			Document doc = Jsoup.connect(url).get();
			
			List<String> songNameList = doc.getElementsByClass("song-name typography-label").eachText();
			List<String> singerNameList = doc.getElementsByClass("by-line typography-caption").eachText();
			List<String> albumImageList = doc.getElementsByClass("media-artwork-v2__image").eachAttr("srcset");
			List<String> albumList = doc.getElementsByClass("col col-album").select("a[href]").eachText();
			
			for(int i=0; i<songNameList.size(); i++) {		
				// 곡 정보 
				String songName = songNameList.get(i);
			
				// 가수 정보
				String singerName = singerNameList.get(i);
				
				// 앨범 정보
				String albumName = albumList.get(i);
				
				// 앨범 이미지 ( 40 / 80 ) 
				String size40 = albumImageList.get(i+1).split(",")[0];	// ITUNES 로고사진이 함께 크롤링되므로 TOP1부터 가져오기 위해 1부터 시작
				String albumImage = size40.substring(0,size40.length()-4);
				
				// 앨범 이미지 어떤 사이즈로 가져올건지에 따라 추후 작업
//				for(int j=1; j<songNameList.size(); j++) {	
//					String []albumImage = albumImageList.get(j).split(",");	
//					String size40 = albumImage[0].substring(0,albumImage[0].length()-4);
//					String size80 = albumImage[1].substring(0,albumImage[1].length()-4);
//				}
				
				System.out.println("순위 : "+(i+1)+"위");
				System.out.println("곡명 : "+songName);
				System.out.println("가수명 : "+singerName);
				System.out.println("앨범명 : "+albumName);
				System.out.println("앨범 이미지 URL : "+albumImage);
				System.out.println();
			}			
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
