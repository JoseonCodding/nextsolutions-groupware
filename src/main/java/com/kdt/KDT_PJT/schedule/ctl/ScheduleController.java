package com.kdt.KDT_PJT.schedule.ctl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

//	@ModelAttribute("navUrl")
//	String navUrl() {
//		return "schedule/nav";
//	}
	
	@Autowired
	ScheduleMapper mapper;
	
	//문자열을 HTML escape 하는 메서드
	public class XssUtils {
	    public static String escapeHtml(String input) {
	        if (input == null) return null;
	        return input.replaceAll("&", "&amp;")
	                    .replaceAll("<", "&lt;")
	                    .replaceAll(">", "&gt;")
	                    .replaceAll("\"", "&quot;")
	                    .replaceAll("'", "&#039;");
	    }
	}

	
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
	    
	    
	    
	    // XSS 처리 적용
	    for (ScheduleDTO dto : scheduleList) {
	        dto.convertDatesToLocal(); 
	        dto.setTitle(XssUtils.escapeHtml(dto.getTitle()));
	        dto.setContent(XssUtils.escapeHtml(dto.getContent())); // 내용 필드도 있다면
	    }

	    model.addAttribute("scheduleList", scheduleList);
		model.addAttribute("mainUrl", "schedule/main");
		

		//return "navTap";
		return "home";
	}
	
	//일정 등록
	@GetMapping("/insert")
	String insert(HttpSession session, Model model) {

		model.addAttribute("mainUrl", "schedule/insert");

		return "home";
	}
	
	@PostMapping("/insert")
	String insertReg(HttpSession session, Model model,  ScheduleDTO dto) {
		
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    dto.setEmployeeId(loginUser.getEmployeeId());
	    
	    // 입력값 XSS 방어
	    dto.setTitle(XssUtils.escapeHtml(dto.getTitle()));
	    dto.setContent(XssUtils.escapeHtml(dto.getContent()));
	    
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

		return "home";
	}
	
	//일정 수정
	@GetMapping("/modify")
	public String scheduleModify(Model model, ScheduleDTO dto) {
		
		ScheduleDTO scheduleDetail = mapper.getScheduleDetail(dto);
		System.out.println("modify : "+scheduleDetail);
		model.addAttribute("scd", scheduleDetail);
		model.addAttribute("mainUrl", "schedule/modify");

		return "home";
	}
	
	
	@PostMapping("/modify")
	public String scheduleModifyReg(HttpSession session,Model model, RedirectAttributes ra, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
	    
	    // 입력값 XSS 방어
	    dto.setTitle(XssUtils.escapeHtml(dto.getTitle()));
	    dto.setContent(XssUtils.escapeHtml(dto.getContent()));
	    
		int modify = mapper.modify(dto);

		System.out.println("modify : "+modify);
		model.addAttribute("modify", modify);
		
		// 수정 성공 여부 메시지 전달
        if (modify>0) {
            ra.addFlashAttribute("msg", "수정되었습니다.");
        } else {
        	ra.addFlashAttribute("msg", "수정사항이 없습니다.");
        }
		
		return "redirect:/schedule";
	}
	
	//일정 삭제
	@RequestMapping("/delete")
	public String scheduledelete(HttpSession session, RedirectAttributes ra, ScheduleDTO dto) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    
	    dto.setEmployeeId(loginUser.getEmployeeId());
		int cnt = mapper.delete(dto);
		System.out.println("delete : "+cnt);
		
		
		// 삭제 성공 여부 메시지 전달
        if (cnt>0) {
            ra.addFlashAttribute("msg", "삭제되었습니다.");
        } else {
        	ra.addFlashAttribute("msg", "삭제가 정상적으로 처리되지 않았습니다.");
        }

		return "redirect:/schedule";
	}

}
