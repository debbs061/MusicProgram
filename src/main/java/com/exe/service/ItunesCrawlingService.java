package com.exe.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import com.exe.domain.ChartDate;
import com.exe.domain.Singer;
import com.exe.domain.Song;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchSongDTO;

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
            date = date.concat(Integer.toString(localDateTime.getMonthValue()));
            date = date.concat(Integer.toString(localDateTime.getDayOfMonth()));

            String time = "";
            time = time.concat(Integer.toString(localDateTime.getHour()));
            time = time.concat(Integer.toString(localDateTime.getMinute()));

            ChartDate chartDate = new ChartDate();
            chartDate.setDate(date);
            chartDate.setTime(time);
            chartDate.setSiteName("ITUNES");     
            chartDAO.insertChartDate(chartDate);                   
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
                    singerKey = "SINGER"+date+time+(i+1);
                    Singer singer = new Singer();
                    singer.setSingerKey(singerKey);
                    singer.setSingerName(singerName);
                    singerDAO.insertSingerInfo(singer);
                }
                
                // 기존 앨범 정보 조회
                SearchAlbumDTO searchAlbumDTO = new SearchAlbumDTO();
                searchAlbumDTO.setAlbumTitle(albumName);
                searchAlbumDTO.setSingerKey(singerKey);
                String albumKey = albumDAO.getAlbumKey(searchAlbumDTO);
                // 등록된 앨범이 없을 경우 INSERT
                if(albumKey == null){
                    albumKey = "ALBUM"+date+time+(i+1);
                    Album album = new Album();
                    album.setAlbumKey(albumKey);
                    album.setSingerKey(singerKey);
                    album.setAlbumTitle(albumName);
                    album.setAlbumImage(albumImgUrl);

                    albumDAO.insertAlbumInfo(album);
                }
                
                // 기존 노래 정보 조회
                SearchSongDTO searchSongDTO = new SearchSongDTO();
                searchSongDTO.setAlbumKey(albumKey);
                searchSongDTO.setSongTitle(songName);
                String songKey = songDAO.getSongKey(searchSongDTO);
                // 등록된 노래가 없을 경우 INSERT
                if(songKey == null){
                    songKey = "SONG"+date+time+(i+1);
                    Song song =  new Song();
                    // ITUNES는 발매일을 첫페이지에서 못가져오는 상태이므로 주석처리
                    //song.setRelDate(updateDateTimeStr);
                    song.setSongTitle(songName);
                    song.setAlbumKey(albumKey);
                    song.setSongKey(songKey);

                    songDAO.insertSongInfo(song);
                }
				
				System.out.println("순위 : "+(i+1)+"위");
				System.out.println("곡명 : "+songName);
				System.out.println("가수명 : "+singerName);
				System.out.println("앨범명 : "+albumName);
				System.out.println("앨범 이미지 URL : "+albumImgUrl);
				System.out.println();
			}			
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}