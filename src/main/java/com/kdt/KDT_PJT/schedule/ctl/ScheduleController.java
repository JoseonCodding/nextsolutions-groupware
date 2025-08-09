package com.kdt.KDT_PJT.schedule.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "schedule/nav";
	}
	
	@Autowired
	ScheduleMapper mapper;
	
	@RequestMapping
	String showSchedulePage(Model model) {
		model.addAttribute("mainUrl", "schedule/main");
		return "navTap";
	}
	
	@GetMapping("/insert")
	String insert(HttpSession session, Model model) {

		model.addAttribute("mainUrl", "schedule/insert");

		return "navTap";
	}
	
	@PostMapping("/insert")
	String insertReg(HttpSession session, Model model,  ScheduleDTO dto) {
		
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
	    
		// System.out.println("보여라"+loginUser.getEmployeeId());
		mapper.insert(dto);
	    
		model.addAttribute("mainUrl", "redirect:/schedule");
		return "navTap";
	}
	
	
}
