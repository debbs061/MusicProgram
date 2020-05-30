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
public class GenieCrawllingService {

	@Autowired
	private SingerDAO singerDAO;
	@Autowired
	private AlbumDAO albumDAO;
	@Autowired
	private SongDAO songDAO;
	@Autowired
	private ChartDAO chartDAO;

	@Transactional
	public void FloChartCrawling() {
		try {
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
			chartDate.setSiteName("GENIE");
			
			chartDAO.insertChartDate(chartDate);

			URL url = new URL("https://www.genie.co.kr/chart/top200");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));

			String line = "";
			String result = "";

			while ((line = reader.readLine()) != null) {
				result = result.concat(line);
			}
			
			JsonArray data = parseJsonData(result);

			for (int i = 0; i < data.size(); i++) {
				JsonObject chartData = (JsonObject) data.get(i);
				String songName = chartData.get("a.title ellipsis").getAsString(); // �뷡 ����
				String updateDateTimeStr = chartData.get("a.title ellipsis").getAsString(); // �뷡 �߸���
				
				// ���� ����
				JsonObject singerInfo = (JsonObject) chartData.get("a.artist ellipsis");
				String singerName = singerInfo.get("a.artist ellipsis").getAsString(); // ���� �̸�

				// �ٹ� ����
				JsonObject albumInfo = (JsonObject) chartData.get("a.albumtitle ellipsis");
				String albumName = albumInfo.get("a.albumtitle ellipsis").getAsString(); // �ٹ� �̸�

				// �ٹ� �̹���
				// size : 75 / 140 / 200 / 350 / 500 / 1000
				JsonArray imgList = (JsonArray) albumInfo.get("imgList");
				String albumImgUrl = ((JsonObject) imgList.get(0)).get("url").getAsString(); // 75 ������ �̹��� url

				// ��ũ ����
				JsonObject rank = (JsonObject) chartData.get("number.rank");
				boolean newYn = rank.get("newYn").getAsBoolean(); // �ű� ���� ����
				int rankBadge = rank.get("rankBadge").getAsInt(); // ���� ����

				// ���� ���� ���� ��ȸ
				String singerKey = singerDAO.getSingerKey(singerName);
				// ��ϵ� ������ ���� ��� INSERT
				if (singerKey == null) {
					singerKey = "SINGER" + date + time + (i + 1);

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
				if (albumKey == null) {
					albumKey = "ALBUM" + date + time + (i + 1);

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
				// ��ϵ� �뷡�� ���� ��� INSERT
				if (songKey == null) {
					songKey = "SONG" + date + time + (i + 1);

					Song song = new Song();
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
				chart.setRank(i + 1);
				chart.setRankChange(rankBadge);
				chart.setSingerKey(singerKey);
				chart.setAlbumKey(albumKey);
				chart.setSongKey(songKey);

				chartDAO.insertChart(chart);

				System.out.println("���� : " + (i + 1) + "��");
				System.out.println("��� : " + songName);
				System.out.println("�߸��� : " + updateDateTimeStr);
				System.out.println("������ : " + singerName);
				System.out.println("�ٹ��� : " + albumName);
				System.out.println("�ٹ� �̹��� URL : " + albumImgUrl);
				System.out.println("��Ʈ �ű� ���� : " + newYn + ", ���� ���� : " + rankBadge);
				System.out.println();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public JsonArray parseJsonData(String data) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
		JsonObject jsonData = (JsonObject) jsonObject.get("data");
		JsonArray trackList = (JsonArray) jsonData.get("trackList");
		return trackList;
	}

}
