package com.exe.dao;

import com.exe.domain.Album;
import com.exe.dto.SearchAlbumDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AlbumDAO {

    @Autowired
    private SqlSessionTemplate sessionTemplate;

    public String getAlbumKey(SearchAlbumDTO searchAlbumDTO){
        return sessionTemplate.selectOne("sourcemapper.getAlbumKey", searchAlbumDTO);
    }

    public void insertAlbumInfo(Album album){
        sessionTemplate.insert("sourcemapper.insertAlbumInfo", album);
    }
}
