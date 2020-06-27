package com.exe.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exe.dao.AlbumDAO;
import com.exe.dao.ChartDAO;
import com.exe.dao.SingerDAO;
import com.exe.dao.SongDAO;
import com.exe.domain.Album;
import com.exe.domain.Chart;
import com.exe.domain.ChartDate;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchSongDTO;
import com.exe.service.YoutubeSearchApi;

@Service
public class ItunesCrawlingService {
	@Autowired
    private SingerDAO singerDAO;
    @Autowired
    private AlbumDAO albumDAO;
    @Autowired
    private SongDAO songDAO;
    @Autowired
    private ChartDAO chartDAO;
	
    @Transactional
	public void ItunesChartCrawling() throws IOException {				
    	try{	
			LocalDateTime localDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            String localDate = timestamp.toString();
            
            String date = "";
            date = date.concat(Integer.toString(localDateTime.getYear()));
            date = date.concat(localDateTime.format(DateTimeFormatter.ofPattern("MM")));
            date = date.concat(localDateTime.format(DateTimeFormatter.ofPattern("dd")));

            String time = "";
            time = time.concat(localDateTime.format(DateTimeFormatter.ofPattern("HH")));
            time = time.concat(localDateTime.format(DateTimeFormatter.ofPattern("mm")));

            ChartDate chartDate = new ChartDate();
            chartDate.setDate(date);
            chartDate.setTime(time);
            chartDate.setSite_name("ITUNES");     
            chartDAO.insertChartDate(chartDate);                   
			String url = "https://music.apple.com/us/playlist/top-100-global/pl.d25f5d1181894928af76c85c967f8f31";
			Document doc = Jsoup.connect(url).get();
			List<String> songNameList = doc.getElementsByClass("song-name typography-label").eachText();
			List<String> singerNameList = doc.getElementsByClass("by-line typography-caption").eachText();
			List<String> albumImageList = doc.getElementsByClass("media-artwork-v2__image").eachAttr("srcset");
			List<String> albumList = doc.getElementsByClass("col col-album").select("a[href]").eachText();
			 
			YoutubeSearchApi obj = new YoutubeSearchApi();
			int cnt = 0; // 유튜브 Api 호출 횟수 제한 용도
			
			for(int i=0; i<songNameList.size(); i++) {	
				
				// 곡 정보 
				String songName = songNameList.get(i);
			
				// 가수 정보
				String singerName = singerNameList.get(i);
				
				// 앨범 정보
				String albumName = albumList.get(i);
				
				// 앨범 이미지 ( 40 / 80 ) 
				String size40 = albumImageList.get(i+1).split(",")[0];	// ITUNES 로고사진이 함께 크롤링되므로 TOP1부터 가져오기 위해 1부터 시작
				String albumImgUrl = size40.substring(0,size40.length()-4);
				
				// 앨범 이미지 어떤 사이즈로 가져올건지에 따라 추후 작업
//				for(int j=1; j<songNameList.size(); j++) {	
//					String []albumImage = albumImageList.get(j).split(",");	
//					String size40 = albumImage[0].substring(0,albumImage[0].length()-4);
//					String size80 = albumImage[1].substring(0,albumImage[1].length()-4);
//				}
				
				
				// 기존 가수 정보 조회
				String singerKey = singerDAO.getSingerKey(singerName);	
                // 등록된 가수가 없을 경우 INSERT
                if(singerKey == null){
                    singerKey = "ISINGER"+date+time+(i+1);
                    Singer singer = new Singer();
                    singer.setSinger_key(singerKey);
                    singer.setSinger_name(singerName);
                    singerDAO.insertSingerInfo(singer);
                }
                
                // 기존 앨범 정보 조회
                SearchAlbumDTO searchAlbumDTO = new SearchAlbumDTO();
                searchAlbumDTO.setAlbum_title(albumName);
                searchAlbumDTO.setSinger_key(singerKey);
                String albumKey = albumDAO.getAlbumKey(searchAlbumDTO);
                // 등록된 앨범이 없을 경우 INSERT
                if(albumKey == null){	
                    albumKey = "IALBUM"+date+time+(i+1);
                    Album album = new Album();
                    album.setAlbum_key(albumKey);
                    album.setSinger_key(singerKey);
                    album.setAlbum_title(albumName);
                    album.setAlbum_image(albumImgUrl);
                    albumDAO.insertAlbumInfo(album);
                }
                // 기존 노래 정보 조회
                SearchSongDTO searchSongDTO = new SearchSongDTO();
                searchSongDTO.setAlbum_key(albumKey);
                searchSongDTO.setSong_title(songName);
                String songKey = songDAO.getSongKey(searchSongDTO);
                String youtubeLink = "";
                
                // 등록된 노래가 없을 경우 INSERT - 유튜브API는 사이트당 20회 미만으로 제한  	            
                if(songKey == null){  
	            	Song song =  new Song();
	            	if(cnt<20) {
	            		youtubeLink = obj.main(songName); 
	            		song.setYoutube_link(youtubeLink);
	            		cnt++; 
	            	}
	                songKey = "ISONG"+date+time+(i+1);                    
	                // ITUNES는 발매일을 첫페이지에서 못가져오는 상태이므로 주석처리
	                //song.setRelDate(updateDateTimeStr);
	                song.setSong_title(songName);
	                song.setAlbum_key(albumKey);
	                song.setSong_key(songKey);
	                
	                songDAO.insertSongInfo(song);
	            }else{
	            	Song song =  new Song();
	            	if(songDAO.getYoutubeLink(songKey) == null) {
		            	if(cnt<20) {
		            		youtubeLink = obj.main(songName);
		            		song.setYoutube_link(youtubeLink);
		            		cnt++; 
		            	}
		                song.setSong_key(songKey);
		                songDAO.updateSongInfo(song);
	            	} 
	             }
                
                // ITUNES 메인페이지에서 순위변동 정보를 크롤링하지 못하므로 계산
                Chart chart = new Chart();
                chart.setSite_name("ITUNES");
                chart.setSinger_key(singerKey); 
                chart.setAlbum_key(albumKey);
                chart.setSong_key(songKey);
                
                // CHART 테이블에 들어온 적 없는 곡이라면 순위변동 0으로 처리
                int rankChange = 0;                
                Integer preRank = chartDAO.getPreRank(chart);                
                preRank = preRank == null ? 0 : preRank;       
                if(preRank != 0){
                	rankChange = preRank - (i+1);
                }     
                
                // 날짜별 차트정보 INSERT
                chart.setDate(chartDate.getDate());
                chart.setTime(chartDate.getTime());
                chart.setRank(i+1);
                chart.setRank_change(rankChange);
                chartDAO.insertChart(chart);
				
				System.out.println("순위 : "+(i+1)+"위");
				System.out.println("곡명 : "+songName);
				System.out.println("가수명 : "+singerName);
				System.out.println("앨범명 : "+albumName);
				System.out.println("앨범 이미지 URL : "+albumImgUrl);
				System.out.println("순위변동 : "+rankChange);
				System.out.println("유튜브링크 : "+youtubeLink);
				System.out.println();
				
			}	
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}