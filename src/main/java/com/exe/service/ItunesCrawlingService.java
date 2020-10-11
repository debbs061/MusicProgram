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
   System.out.println("itunesCrawling을 시작합니다"); 		
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
			String url = "https://music.apple.com/us/playlist/top-100-south-korea/pl.d3d10c32fbc540b38e266367dc8cb00c";
			Document doc = Jsoup.connect(url).get();
			List<String> songNameList = doc.getElementsByClass("song-name typography-label").eachText();
			List<String> singerNameList = doc.getElementsByClass("by-line typography-caption").eachText();
			List<String> albumImageList = doc.getElementsByClass("media-artwork-v2__image").eachAttr("srcset");
			List<String> albumList = doc.getElementsByClass("col col-album").select("a[href]").eachText();
			 
			YoutubeSearchApi obj = new YoutubeSearchApi();
			int cnt = 0; // ��Ʃ�� Api ȣ�� Ƚ�� ���� �뵵
			
			for(int i=0; i<songNameList.size(); i++) {	
				
				// �� ���� 
				String songName = songNameList.get(i);
			
				// ���� ����
				String singerName = singerNameList.get(i);
				
				// �ٹ� ����
				String albumName = albumList.get(i);
				
				// �ٹ� �̹��� ( 40 / 80 ) 
				String size40 = albumImageList.get(i+1).split(",")[0];	// ITUNES �ΰ������ �Բ� ũ�Ѹ��ǹǷ� TOP1���� �������� ���� 1���� ����
				String albumImgUrl = size40.substring(0,size40.length()-4);
				
				// �ٹ� �̹��� � ������� �����ð����� ���� ���� �۾�
//				for(int j=1; j<songNameList.size(); j++) {	
//					String []albumImage = albumImageList.get(j).split(",");	
//					String size40 = albumImage[0].substring(0,albumImage[0].length()-4);
//					String size80 = albumImage[1].substring(0,albumImage[1].length()-4);
//				}
				
				
				// ���� ���� ���� ��ȸ
				String singerKey = singerDAO.getSingerKey(singerName);	
                // ��ϵ� ������ ���� ��� INSERT
                if(singerKey == null){
                    singerKey = "ISINGER"+date+time+(i+1);
                    Singer singer = new Singer();
                    singer.setSinger_key(singerKey);
                    singer.setSinger_name(singerName);
                    singerDAO.insertSingerInfo(singer);
                }
                
                // ���� �ٹ� ���� ��ȸ
                SearchAlbumDTO searchAlbumDTO = new SearchAlbumDTO();
                searchAlbumDTO.setAlbum_title(albumName);
                searchAlbumDTO.setSinger_key(singerKey);
                String albumKey = albumDAO.getAlbumKey(searchAlbumDTO);
                // ��ϵ� �ٹ��� ���� ��� INSERT
                if(albumKey == null){	
                    albumKey = "IALBUM"+date+time+(i+1);
                    Album album = new Album();
                    album.setAlbum_key(albumKey);
                    album.setSinger_key(singerKey);
                    album.setAlbum_title(albumName);
                    album.setAlbum_image(albumImgUrl);
                    albumDAO.insertAlbumInfo(album);
                }
                // ���� �뷡 ���� ��ȸ
                SearchSongDTO searchSongDTO = new SearchSongDTO();
                searchSongDTO.setAlbum_key(albumKey);
                searchSongDTO.setSong_title(songName);
                String songKey = songDAO.getSongKey(searchSongDTO);
                String youtubeLink = "";
                
                // ��ϵ� �뷡�� ���� ��� INSERT - ��Ʃ��API�� ����Ʈ�� 20ȸ �̸����� ����  	            
                if(songKey == null){  
	            	Song song =  new Song();
	            	if(cnt<20) {
	            		youtubeLink = obj.main(songName); 
	            		song.setYoutube_link(youtubeLink);
	            		cnt++; 
	            	}
	                songKey = "ISONG"+date+time+(i+1);                    
	                // ITUNES�� �߸����� ù���������� ���������� �����̹Ƿ� �ּ�ó��
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
                
                // ITUNES �������������� �������� ������ ũ�Ѹ����� ���ϹǷ� ���
                Chart chart = new Chart();
                chart.setSite_name("ITUNES");
                chart.setSinger_key(singerKey); 
                chart.setAlbum_key(albumKey);
                chart.setSong_key(songKey);
                
                // CHART ���̺� ���� �� ���� ���̶�� �������� 0���� ó��
                int rankChange = 0;                
                Integer preRank = chartDAO.getPreRank(chart);                
                preRank = preRank == null ? 0 : preRank;       
                if(preRank != 0){
                	rankChange = preRank - (i+1);
                }     
                
                // ��¥�� ��Ʈ���� INSERT
                chart.setDate(chartDate.getDate());
                chart.setTime(chartDate.getTime());
                chart.setRank(i+1);
                chart.setRank_change(rankChange);
                chartDAO.insertChart(chart);
				
				System.out.println("���� : "+(i+1)+"��");
				System.out.println("��� : "+songName);
				System.out.println("������ : "+singerName);
				System.out.println("�ٹ��� : "+albumName);
				System.out.println("�ٹ� �̹��� URL : "+albumImgUrl);
				System.out.println("�������� : "+rankChange);
				System.out.println("��Ʃ�긵ũ : "+youtubeLink);
				System.out.println();
				
			}	
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}