package com.exe.service;

import com.exe.domain.Chart;
import com.exe.domain.ChartDate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class FloCrawlingService {

    public void FloChartCrawling(){
        try{
            LocalDateTime localDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            String localDate = timestamp.toString();
            String date = localDate.split(" ")[0];
            String time = localDate.split(" ")[1].split("\\.")[0];

            ChartDate chartDate = new ChartDate();
            chartDate.setDate(date);
            chartDate.setTime(time);
            chartDate.setSiteName("FLO");

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
                JsonObject singer = (JsonObject)chartData.get("representationArtist");
                String singerName = singer.get("name").getAsString();   //가수 이름

                // 앨범 정보
                JsonObject album = (JsonObject)chartData.get("album");
                String albumName = album.get("title").getAsString();    //앨범 이름

                // 앨범 이미지
                // size : 75 / 140 / 200 / 350 / 500 / 1000
                JsonArray imgList = (JsonArray)album.get("imgList");
                String albumImgUrl = ((JsonObject)imgList.get(0)).get("url").getAsString(); // 75 사이즈 이미지 url

                // 랭크 정보
                JsonObject rank = (JsonObject)chartData.get("rank");
                boolean newYn = rank.get("newYn").getAsBoolean();   //신규 진입 여부
                int rankBadge = rank.get("rankBadge").getAsInt();   //순위 변동

                Chart chart = new Chart();
                chart.setDate(chartDate.getDate());
                chart.setTime(chartDate.getTime());
                chart.setSiteName("FLO");
                chart.setRank(i+1);
                chart.setRankChange(rankBadge);

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
