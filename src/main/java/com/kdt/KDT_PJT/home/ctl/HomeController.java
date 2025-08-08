package com.kdt.KDT_PJT.home.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
	String Home(Model mm) {
		System.out.println("Home 진입");
		mm.addAttribute("mainUrl", "testtt/asdf");
		return "login/loginForm";
	}

}
