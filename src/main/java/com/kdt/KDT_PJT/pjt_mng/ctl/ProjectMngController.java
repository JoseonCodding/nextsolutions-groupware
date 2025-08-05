package com.kdt.KDT_PJT.pjt_mng.ctl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pjtMng")
public class ProjectMngController {
	
	
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());
	

	@GetMapping("/getPjtList")
	public String getPjtList(Model model) {
		
		log.info("getPjtList Called >>> ");
		
		// model.addAttribute("name", "홍길동");
		return "pjt_main";
	}


}
