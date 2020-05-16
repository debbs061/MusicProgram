package com.exe.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
	
	@RequestMapping(value = "/getSample", produces = "text/plain; charset=UTF-8")
	public String getText() {
		
		return "¸á·Ð ¹ÂÁ÷ÀÔ´Ï´Ù"; 
	}

}
