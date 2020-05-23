package com.exe.dao;

import com.exe.domain.Singer;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SingerDAO {

    @Autowired
    private SqlSessionTemplate sessionTemplate;

    public String getSingerKey(String singerName){
        return sessionTemplate.selectOne("sourcemapper.getSingerKey", singerName);
    }

    public void insertSingerInfo(Singer singer){
        sessionTemplate.insert("sourcemapper.insertSingerInfo", singer);
    }
}
