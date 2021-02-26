# Music Chart

Music Chart 는 음악차트사이트 'FLO'와 'ITUNES' 의 Top-100 차트 데이터를 JSON 형태로 제공하는 REST-API 입니다.

******동영상넣기******


### Folder Structure

    .
    ├── src/main/java                   
    │            ├── com/exe/dao
    │            │			 ├── AlbumDAO.java
    │            │			 ├── ChartDAO.java
    │            │			 ├── SingerDAO.java
    │            │			 └── SongDAO.java
    │            ├── com/exe/domain
    │            │			 ├── Album.java
    │            │			 ├── Chart.java
    │            │			 ├── ChartDate.java
    │            │			 ├── Singer.java
    │            │			 └── Song.java
    │            ├── com/exe/dto
    │            │			 ├── SearchAlbumDTO.java
    │            │			 ├── SearchChartDTO.java
    │            │			 ├── SearchSingerDTO.java
    │            │			 ├── SearchSongDTO.java
    │            │			 └── SearchWordDTO.java		
    │            ├── com/exe/musicchart
    │            │			 ├── MusicController.java
    │            │			 └── RestMusicController.java		
    │            ├── com/exe/service
    │            │			 ├── ApiService.java
    │            │			 ├── ApiServiceImpl.java
    │            │			 ├── FloCrawlingService.java
    │            │			 ├── ItunesCrawlingService.java
    │            │			 └── YoutubeSearchApi.java		
    │            └── com/exe/task
    │               		 └── CrawlingTask.java	
    ├── src/main/resources 
    │   		 ├── com/exe/mapper
    │            │		   └── sourceMapper.xml	
    │            ├── log4j.xml
    │            └── mybatis-config.xml
    ├── src/main/webapp
    │            └── WEB-INF
    │            	├── spring
    │            	│	├── appServlet
    │            	│	│   └── sevlet-context.xml
    │            	│	└── root-context.xml
    │                └── web.xml
    └── pom.xml

### REST API
| URL                       | Description               | Example                   |
|---------------------------|---------------------------|---------------------------|
| /api/getTodayChart/{date} | 해당 날짜의 Top100 데이터가 반환됩니다  | /api/getTodayChart/210227 |
| /api/getInfo/{searchWord} | 해당 키워드가 들어간 검색결과가 반환됩니다   | /api/getInfo/아이유          |


### How it all fits together (파일구조 도식형식)
**Status:** Optional.

**Requirements:**
- Must not have its own title.
- Must link to local image in current repository.
