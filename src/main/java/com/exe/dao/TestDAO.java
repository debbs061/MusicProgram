package com.exe.dao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.exe.dto.TestDTO;

public class TestDAO {

	private SqlSessionTemplate sessionTemplate;

	public void setSessionTemplate(SqlSessionTemplate sessionTemplate)
			throws Exception {
		
		this.sessionTemplate = sessionTemplate;

	} 

	public TestDTO getData(){

		TestDTO res = 
				sessionTemplate.selectOne("sourcemapper.getData");
		
		return res;
	}

	
}

