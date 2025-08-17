package com.kdt.KDT_PJT.schedule.ctl;

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
	
	//일정 메인 페이지
	@RequestMapping
	String showSchedulePage(HttpSession session, Model model, ScheduleDTO schDto) {
		
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
		schDto.setEmployeeId(loginUser.getEmployeeId());
		
		// DTO에 이번 달 시작일·종료일 세팅
	    //schDto.monthDays();
		model.addAttribute("firstDayOfWeek", schDto.getFirstDayOfWeek());  // 1=월요일 ~ 7=일요일
		
	    List<ScheduleDTO> scheduleList = mapper.getScheduleListByMonth(schDto);
	    scheduleList.addAll(mapper.getProjectListByMonth());
	    
	    
	    
	    for (ScheduleDTO dto : scheduleList) {
	        dto.convertDatesToLocal();
	    }

	    model.addAttribute("scheduleList", scheduleList);
		model.addAttribute("mainUrl", "schedule/main");
		

		return "navTap";
	}
	
	//일정 등록
	@GetMapping("/insert")
	String insert(HttpSession session, Model model) {

		model.addAttribute("mainUrl", "schedule/insert");

		return "navTap";
	}
	
	@PostMapping("/insert")
	String insertReg(HttpSession session, Model model,  ScheduleDTO dto) {
		
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
	    
		mapper.insert(dto);
	    
		return "redirect:/schedule";
	}
	
	//일정 상세보기
	@RequestMapping("/detail")
	public String scheduleDetail(Model model, ScheduleDTO dto) {
		
		ScheduleDTO scheduleDetail = mapper.getScheduleDetail(dto);
		System.out.println("detail : "+scheduleDetail);
		model.addAttribute("scd", scheduleDetail);
		model.addAttribute("mainUrl", "schedule/detail");

		return "navTap";
	}
	
	//일정 수정
	@GetMapping("/modify")
	public String scheduleModify(Model model, ScheduleDTO dto) {
		
		ScheduleDTO scheduleDetail = mapper.getScheduleDetail(dto);
		System.out.println("modify : "+scheduleDetail);
		model.addAttribute("scd", scheduleDetail);
		model.addAttribute("mainUrl", "schedule/modify");

		return "navTap";
	}
	
	
	@PostMapping("/modify")
	public String scheduleModifyReg(HttpSession session,Model model, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
		int modify = mapper.modify(dto);
		System.out.println("modify : "+modify);
		model.addAttribute("modify", modify);
		
		return "redirect:/schedule";
	}
	
	//일정 삭제
	@RequestMapping("/delete")
	public String scheduledelete(HttpSession session, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
		int cnt = mapper.delete(dto);
		System.out.println("delete : "+cnt);
		
		
		return "redirect:/schedule";
	}

}
