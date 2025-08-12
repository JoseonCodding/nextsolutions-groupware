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
	String showSchedulePage(HttpSession session, Model model, ScheduleDTO schDto) {

//		    LocalDate now = LocalDate.now();
//		    LocalDate firstDay = now.withDayOfMonth(1);

//		    LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
//
//		    Date startDate = java.sql.Date.valueOf(firstDay);
//		    Date endDate = java.sql.Date.valueOf(lastDay);
		
		// DTO에 이번 달 시작일·종료일 세팅
	    schDto.monthDays();
		model.addAttribute("firstDayOfWeek", schDto.getFirstDayOfWeek());  // 1=월요일 ~ 7=일요일
		
		
		
	    List<ScheduleDTO> scheduleList = mapper.getScheduleListByMonth(schDto);
	    
	    for (ScheduleDTO dto : scheduleList) {
	        dto.convertDatesToLocal();
	    }

	    model.addAttribute("scheduleList", scheduleList);
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
