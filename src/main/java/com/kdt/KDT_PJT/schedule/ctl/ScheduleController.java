package com.kdt.KDT_PJT.schedule.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "schedule/nav";
	}
	
	ScheduleMapper mapper;
	
	@RequestMapping
	String showSchedulePage(Model model) {
		model.addAttribute("mainUrl", "schedule/main");
		return "navTap";
	}
	
	@GetMapping("/insert")
	String insert(Model model,  ScheduleDTO dto) {
		
		int res = mapper.insert(dto);
		
		model.addAttribute("mainUrl", "schedule/insert");
		return "navTap";
	}
	
	
}
