package com.kdt.KDT_PJT.attend.ctl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;


@Controller
@RequestMapping("/attend")
public class AttendController {

	@Autowired
    Attendance service;


    @GetMapping
    String showAttendancePage(Model model) {
    	
    	
    	List<AttendDTO> attendList = service.getAttendData(); 
    	
    	System.out.println("showAttendancePage:"+attendList);
        model.addAttribute("mainData", attendList);
        
        model.addAttribute("mainUrl", "attend/check");
        return "home";
        
    }

    //출근 시간 기록
    @PostMapping("/in")
    String checkIn() {
    	System.out.println("checkIn 작동하나");
        service.recordCheckIn();
        return "redirect:/attend";
    }
    
    //퇴근 시간 기록
    @PostMapping("/out")
    String checkOut() {
    	System.out.println("checkOut 작동");
        service.recordCheckOut();
        return "redirect:/attend";
    }
    
    //출퇴근 기록 목록 (관리자용)
    @GetMapping("/attendList")
    public String attendPage1(Model model) {
        List<AttendDTO> attendList = service.getAttendData(); 
        model.addAttribute("mainData", attendList);
        model.addAttribute("mainUrl", "attend/attendList");
        return "home"; 
    }
    
    //출퇴근 기록 수정 신청 
    @GetMapping("/attendTimeInsert")
    String attendTimeInsert(Model model) {
    	System.out.println("attendTimeInsert 작동하나");
        model.addAttribute("mainUrl", "attend/attendTimeInsert");
        return "home";
    }
    
  
    
}
