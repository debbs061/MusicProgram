package com.exe.test;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
//import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

public class YoutubeSearchApi {
	
	// return 받을 video 갯수 설정
    private static final long NUMBER_OF_VIDEOS_RETURNED = 3;

    private static YouTube youtube;

    @Test
    public void main() {
    	
        try {
        	
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("MusicProgram").build();    

            // 파라미터로 넘길 쿼리 (콘솔창에서 파라미터값 입력 테스트)
            String queryTerm = getInputQuery();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            String apiKey = "AIzaSyAUGq4U3p_rLSqQuyFgezx6sQFRwoogiCA";
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // 넘길 수 있는 파라미터 유형 - 아래 url 참고
            // https://developers.google.com/youtube/v3/docs/search/list#parameters
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/publishedAt)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            // 2020-01-01 년 이후로 올라온 동영상만 가지고 오도록 함 -> Songs 테이블에  각 곡에 대한 발매일 데이터가 있으니까 그 이후의 유튜브 영상만 가지고 오도록 설정하면 더 정확할 듯
            search.setPublishedAfter(DateTime.parseRfc3339("2020-01-01T00:00:00Z")); 

            // API 호출
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }

    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
    	
    	HtmlUtils esc = null;
        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();            	

            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id: " + rId.getVideoId()); // videoId가 TgOu00Mf3kI인 경우, https://www.youtube.com/watch?v=TgOu00Mf3kI 만 붙여주면  해당 영상 URI
                System.out.println(" Title: " + esc.htmlUnescape(singleVideo.getSnippet().getTitle())); 
                System.out.println(" Thumbnail: " + thumbnail.getUrl()); // 썸네일 이미지 url
                System.out.println(" PublishedAfter: " + singleVideo.getSnippet().getPublishedAt()); // 영상 업로드일
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }
}