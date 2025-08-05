package com.kdt.KDT_PJT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
	String Home() {
		System.out.println("Home 진입");
		return "index";
	}
}
