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
            chartDate.setSiteName("ITUNES");     
            chartDAO.insertChartDate(chartDate);                  
			String url = "https://music.apple.com/us/playlist/top-100-global/pl.d25f5d1181894928af76c85c967f8f31";
			Document doc = Jsoup.connect(url).get();
			List<String> songNameList = doc.getElementsByClass("song-name typography-label").eachText();
			List<String> singerNameList = doc.getElementsByClass("by-line typography-caption").eachText();
			List<String> albumImageList = doc.getElementsByClass("media-artwork-v2__image").eachAttr("srcset");
			List<String> albumList = doc.getElementsByClass("col col-album").select("a[href]").eachText();
			
			YoutubeSearchApi obj = new YoutubeSearchApi();
			
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
                    singer.setSingerKey(singerKey);
                    singer.setSingerName(singerName);
                    singerDAO.insertSingerInfo(singer);
                }
                
                // ���� �ٹ� ���� ��ȸ
                SearchAlbumDTO searchAlbumDTO = new SearchAlbumDTO();
                searchAlbumDTO.setAlbumTitle(albumName);
                searchAlbumDTO.setSingerKey(singerKey);
                String albumKey = albumDAO.getAlbumKey(searchAlbumDTO);
                // ��ϵ� �ٹ��� ���� ��� INSERT
                if(albumKey == null){	
                    albumKey = "IALBUM"+date+time+(i+1);
                    Album album = new Album();
                    album.setAlbumKey(albumKey);
                    album.setSingerKey(singerKey);
                    album.setAlbumTitle(albumName);
                    album.setAlbumImage(albumImgUrl);
                    albumDAO.insertAlbumInfo(album);
                }
                // ���� �뷡 ���� ��ȸ
                SearchSongDTO searchSongDTO = new SearchSongDTO();
                searchSongDTO.setAlbumKey(albumKey);
                searchSongDTO.setSongTitle(songName);
                String songKey = songDAO.getSongKey(searchSongDTO);
                String youtubeLink = "";
                // ��ϵ� �뷡�� ���� ��� INSERT                
                if(songKey == null){                	
                	youtubeLink = obj.main(songName);
                    songKey = "ISONG"+date+time+(i+1);
                    Song song =  new Song();
                    // ITUNES�� �߸����� ù���������� ���������� �����̹Ƿ� �ּ�ó��
                    //song.setRelDate(updateDateTimeStr);
                    song.setSongTitle(songName);
                    song.setAlbumKey(albumKey);
                    song.setSongKey(songKey);
                    song.setYoutubeLink(youtubeLink);
 
                    songDAO.insertSongInfo(song);
                }
                
                // ITUNES �������������� �������� ������ ũ�Ѹ����� ���ϹǷ� ���
                Chart chart = new Chart();
                chart.setSiteName("ITUNES");
                chart.setSingerKey(singerKey); 
                chart.setAlbumKey(albumKey);
                chart.setSongKey(songKey);
                
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
                chart.setRankChange(rankChange);
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