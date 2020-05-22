package com.exe.dao;

import com.exe.domain.Chart;
import com.exe.domain.ChartDate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChartDAO {
    @Autowired
    private SqlSessionTemplate sessionTemplate;

    public void insertChartDate(ChartDate chartDate){
        sessionTemplate.insert("sourcemapper.insertChartDate", chartDate);
    }

    public void insertChart(Chart chart){
        sessionTemplate.insert("sourcemapper.insertChart", chart);
    }
}
