package com.kdt.KDT_PJT.attend.ctl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/attend")
public class AttendController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "attend/nav";
	}
	
	@Autowired
    Attendance service;

	//사용자 본인의 출퇴근 기록 (근태관리 메인 페이지)
    @GetMapping
    String showAttendancePage(HttpSession session, Model model) {
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	//List<AttendDTO> attendList = service.userAttendList(); 
    	List<AttendDTO> attendList = service.getUserAttendData(loginUser); 
 
    	//System.out.println("사용자 인식: "+empDto.getEmployeeId());
    	System.out.println("showAttendancePage:"+attendList);
    	
        model.addAttribute("mainData", attendList);
        model.addAttribute("mainUrl", "attend/check");
        return "navTap";
        
    }

    //출근 시간 기록
    @PostMapping("/in")
    String checkIn(HttpSession session) {
    	System.out.println("checkIn 작동하나");
        service.recordCheckIn(session);
        return "redirect:/attend";
    }
    
    //퇴근 시간 기록
    @PostMapping("/out")
    String checkOut(HttpSession session) {
    	System.out.println("checkOut 작동");
        service.recordCheckOut(session);
        return "redirect:/attend";
    }
    
    //출퇴근 기록 수정 신청 
    @GetMapping("/attendTimeInsert")
    String attendTimeInsert(Model model) {
    	System.out.println("attendTimeInsert 작동하나");
        model.addAttribute("mainUrl", "attend/attendTimeInsert");
        return "navTap";
    }
    
    //근태 관리자 페이지(출퇴근 변경이력 포함)
    @GetMapping("/attendList")
    public String attendListPage(Model model) {
        List<AttendDTO> attendList = service.getAttendData();
        model.addAttribute("mainData", attendList);
        model.addAttribute("mainUrl", "attend/attendList");
        return "navTap"; 
    }
   
 // 근태 관리자 페이지 - 검색 기능 포함
//    @GetMapping("/attendList")
//    public String attendListPage(
//        @RequestParam(required = false) String workDate,
//        @RequestParam(required = false) String empNm,
//        @RequestParam(required = false) String modifiedBy,
//        Model model) {
//
//        // 검색 조건이 없으면 오늘 출근한 사람만 보여줌
//        List<AttendDTO> attendList;
//        if (workDate == null && empNm == null && modifiedBy == null) {
//            attendList = service.getTodayAttendData();
//        } else {
//            attendList = service.searchAttendData(workDate, empNm, modifiedBy);
//        }
//
//        model.addAttribute("mainData", attendList);
//        model.addAttribute("mainUrl", "attend/attendList");
//        return "navTap"; 
//    }
    //오류 해결하기

    

}
