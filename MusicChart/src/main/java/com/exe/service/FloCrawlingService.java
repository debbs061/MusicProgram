package com.exe.service;

import com.exe.dao.AlbumDAO;
import com.exe.dao.ChartDAO;
import com.exe.dao.SingerDAO;
import com.exe.dao.SongDAO;
import com.exe.domain.*;
import com.exe.dto.SearchAlbumDTO;
import com.exe.dto.SearchSongDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class FloCrawlingService {
    @Autowired
    private SingerDAO singerDAO;
    @Autowired
    private AlbumDAO albumDAO;
    @Autowired
    private SongDAO songDAO;
    @Autowired
    private ChartDAO chartDAO;


    @Transactional
    public void FloChartCrawling(){
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
            chartDate.setSiteName("FLO");

            chartDAO.insertChartDate(chartDate);

            URL url = new URL("https://www.music-flo.com/api/display/v1/browser/chart/1/track/list?size=100");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));

            String line = "";
            String result = "";

            while((line = reader.readLine()) != null){
                result = result.concat(line);
            }

            JsonArray data = parseJsonData(result);

            for(int i=0; i<data.size(); i++){
                JsonObject chartData = (JsonObject)data.get(i);
                String songName = chartData.get("name").getAsString();  //노래 제목
                String updateDateTimeStr = chartData.get("updateDateTime").getAsString();   //노래 발매일

                // 가수 정보
                JsonObject singerInfo = (JsonObject)chartData.get("representationArtist");
                String singerName = singerInfo.get("name").getAsString();   //가수 이름

                // 앨범 정보
                JsonObject albumInfo = (JsonObject)chartData.get("album");
                String albumName = albumInfo.get("title").getAsString();    //앨범 이름

                // 앨범 이미지
                // size : 75 / 140 / 200 / 350 / 500 / 1000
                JsonArray imgList = (JsonArray)albumInfo.get("imgList");
                String albumImgUrl = ((JsonObject)imgList.get(0)).get("url").getAsString(); // 75 사이즈 이미지 url

                // 랭크 정보
                JsonObject rank = (JsonObject)chartData.get("rank");
                boolean newYn = rank.get("newYn").getAsBoolean();   //신규 진입 여부
                int rankBadge = rank.get("rankBadge").getAsInt();   //순위 변동

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
                    song.setRelDate(updateDateTimeStr);
                    song.setSongTitle(songName);
                    song.setAlbumKey(albumKey);
                    song.setSongKey(songKey);

                    songDAO.insertSongInfo(song);
                }

                Chart chart = new Chart();
                chart.setDate(chartDate.getDate());
                chart.setTime(chartDate.getTime());
                chart.setSiteName("FLO");
                chart.setRank(i+1);
                chart.setRankChange(rankBadge);
                chart.setSingerKey(singerKey);
                chart.setAlbumKey(albumKey);
                chart.setSongKey(songKey);

                chartDAO.insertChart(chart);


                System.out.println("순위 : "+(i+1)+"위");
                System.out.println("곡명 : "+songName);
                System.out.println("발매일 : "+updateDateTimeStr);
                System.out.println("가수명 : "+singerName);
                System.out.println("앨범명 : "+albumName);
                System.out.println("앨범 이미지 URL : "+albumImgUrl);
                System.out.println("차트 신규 진입 : "+newYn+", 순위 변동 : "+rankBadge);
                System.out.println();
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public JsonArray parseJsonData(String data){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(data);
        JsonObject jsonData = (JsonObject)jsonObject.get("data");
        JsonArray trackList = (JsonArray) jsonData.get("trackList");
        return trackList;
    }
}
