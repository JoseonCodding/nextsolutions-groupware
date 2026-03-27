package com.kdt.KDT_PJT.schedule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.cmmn.util.XssUtils;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.service.ScheduleService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	@Autowired
	ScheduleService scheduleService;
	
	@ModelAttribute("viewMode")
	String viewMode(@RequestParam(value="viewMode", defaultValue="month") String viewMode) {
		return viewMode;
	}

	
	//일정 메인 페이지
	@RequestMapping
	String showSchedulePage(HttpSession session, Model model, ScheduleDTO schDto
			) {

		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

		schDto.setEmployeeId(loginUser.getEmployeeId());
		schDto.setCompanyId(loginUser.getCompanyId());

		// DTO에 이번 달 시작일·종료일 세팅
	    //schDto.monthDays();
		model.addAttribute("firstDayOfWeek", schDto.getFirstDayOfWeek());  // 1=월요일 ~ 7=일요일

	    List<ScheduleDTO> scheduleList = scheduleService.getScheduleList(schDto);
	    // getScheduleList 내에서 프로젝트 목록 포함
	    
	    
	    
	    // XSS 처리 적용
	    for (ScheduleDTO dto : scheduleList) {
	        dto.convertDatesToLocal(); 
	        dto.setTitle(XssUtils.escape(dto.getTitle()));
	        dto.setContent(XssUtils.escape(dto.getContent())); // 내용 필드도 있다면
	    }

	    model.addAttribute("scheduleList", scheduleList);
		model.addAttribute("mainUrl", "schedule/main");
		//model.addAttribute("viewMode", viewMode); // 뷰 모드 전달

		//return "navTap";
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
	    dto.setCompanyId(loginUser.getCompanyId());
	    
	    // 입력값 XSS 방어
	    dto.setTitle(XssUtils.escape(dto.getTitle()));
	    dto.setContent(XssUtils.escape(dto.getContent()));
	    
		scheduleService.insert(dto);
	    
		return "redirect:/schedule";
	}
	
	//일정 상세보기
	@RequestMapping("/detail")
	public String scheduleDetail(Model model, ScheduleDTO dto) {

		ScheduleDTO scheduleDetail = scheduleService.getDetail(dto);
		model.addAttribute("scd", scheduleDetail);
		model.addAttribute("mainUrl", "schedule/detail");
		

		return "navTap";
	}
	
	//일정 수정
	@GetMapping("/modify")
	public String scheduleModify(Model model, ScheduleDTO dto) {

		ScheduleDTO scheduleDetail = scheduleService.getDetail(dto);
		model.addAttribute("scd", scheduleDetail);
		model.addAttribute("mainUrl", "schedule/modify");
		

		return "navTap";
	}
	
	
	@PostMapping("/modify")
	public String scheduleModifyReg(HttpSession session,Model model, RedirectAttributes ra, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
	    
		int modify = scheduleService.modify(dto);
		model.addAttribute("modify", modify);

		return "redirect:/schedule/detail?scheduleId=" + dto.getScheduleId();
	}
	
	//일정 삭제
	@RequestMapping("/delete")
	public String scheduleDelete(HttpSession session, RedirectAttributes ra, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
		int cnt = scheduleService.delete(dto);


		// 삭제 성공 여부 메시지 전달
        if (cnt>0) {
            ra.addFlashAttribute("msg", "삭제되었습니다.");
        } else {
        	ra.addFlashAttribute("msg", "삭제가 정상적으로 처리되지 않았습니다.");
        }

		return "redirect:/schedule";
	}

}
