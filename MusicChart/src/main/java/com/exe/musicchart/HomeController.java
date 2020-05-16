package com.exe.musicchart;

import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.exe.dao.TestDAO;
import com.exe.dto.TestDTO;

@Controller
public class HomeController {

	@Autowired
	TestDAO testDao;
	
	@RequestMapping(value = "/", method = {RequestMethod.GET,RequestMethod.POST})
	public String index(HttpServletRequest request) {
		
		TestDTO dto= testDao.getData();
		request.setAttribute("dto",dto );

		return "index";
	}

	
	
	
}
