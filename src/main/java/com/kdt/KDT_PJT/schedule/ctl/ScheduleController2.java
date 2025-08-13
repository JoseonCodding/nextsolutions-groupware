package com.kdt.KDT_PJT.schedule.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.schedule.model.ScheduleMapper2;

@Controller
@RequestMapping("/schedule")
public class ScheduleController2 {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "schedule/nav";
	}
	
	@Autowired
	ScheduleMapper2 mapper;
	

}
