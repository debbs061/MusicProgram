package com.exe.musicchart;

import javax.servlet.http.HttpServletRequest;

import com.exe.service.FloCrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.exe.dao.TestDAO;

@RestController
public class HomeController {

	@Autowired
	TestDAO testDao;

    @Autowired
    FloCrawlingService floCrawlingService;

	@RequestMapping(value = "/", method = {RequestMethod.GET,RequestMethod.POST})
	public String index(HttpServletRequest request) {

		/*TestDTO dto= testDao.getData();
		request.setAttribute("dto",dto );*/

		return "index";
	}


	@RequestMapping(value = "/floCrawling", method = {RequestMethod.GET})
    public void getFloCrawling(){
	    floCrawlingService.FloChartCrawling();
    }
}
