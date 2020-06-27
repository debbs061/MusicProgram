package com.exe.dao;

import com.exe.domain.Song;
import com.exe.dto.SearchSongDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SongDAO {
    @Autowired
    private SqlSessionTemplate sessionTemplate;    
        
    public String getSongKey(SearchSongDTO searchSongDTO){
        return sessionTemplate.selectOne("sourcemapper.getSongKey", searchSongDTO);
    }
    
    public String getYoutubeLink(String youtubeLink){
        return sessionTemplate.selectOne("sourcemapper.getYoutubeLink", youtubeLink);
    }

    public void insertSongInfo(Song song){
        sessionTemplate.insert("sourcemapper.insertSongInfo", song);
    }
    
    public void updateSongInfo(Song song){
        sessionTemplate.update("sourcemapper.updateSongInfo", song);
    }
}
