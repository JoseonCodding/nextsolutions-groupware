package com.kdt.KDT_PJT.schedule.ctl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
	String showSchedulePage(HttpSession session,Model model) {

		    LocalDate now = LocalDate.now();
		    LocalDate firstDay = now.withDayOfMonth(1);
		    model.addAttribute("firstDayOfWeek", firstDay.getDayOfWeek().getValue());  // 1=월요일 ~ 7=일요일
		    LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

		    Date startDate = java.sql.Date.valueOf(firstDay);
		    Date endDate = java.sql.Date.valueOf(lastDay);

		    List<ScheduleDTO> scheduleList = mapper.getScheduleListByMonth(
		    		startDate,endDate);
		    
		    for (ScheduleDTO dto : scheduleList) {
		        dto.convertDatesToLocal();
		    }


		    model.addAttribute("scheduleList", scheduleList);
		    model.addAttribute("year", now.getYear());
		    model.addAttribute("month", now.getMonthValue());
		
		
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
	    
		return "redirect:/schedule";
	}
	
}
