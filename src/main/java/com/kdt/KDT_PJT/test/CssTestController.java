package com.kdt.KDT_PJT.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/css")
public class CssTestController {


	
	//일정 등록
	@GetMapping("")
	String test() {

		return "/cssTest";
	}
	

}
