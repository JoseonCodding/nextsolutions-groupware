package com.kdt.KDT_PJT.attend.ctl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.attend.di.AnnualLeave;
import com.kdt.KDT_PJT.attend.model.LeaveDTO;

@Controller
@RequestMapping("/attend/{service}")
public class LeaveController {

	@Autowired
	AnnualLeave service;
	
	
	//연차 관리(사용자용)
    @GetMapping
    public String leaveList(Model model) {
        System.out.println("연차 관리 페이지");
        LeaveDTO dto = service.getAnnualLeaveOne(); 
        System.out.println("/attend/leave : "+dto);
        // 홈에서 뜨는 화면 연결
        model.addAttribute("mainUrl", "attend/leave/leaveList");
        
        model.addAttribute("listData", dto);
        return "home"; 
    }
    
    //연차 사용 신청(사용자용)
    @RequestMapping("/insert")
    public String insert(Model model) {
    	System.out.println("연차 신청 페이지");
    	model.addAttribute("mainUrl", "attend/leave/insert");
        return "home"; 
    }
    
    //연차 관리(관리자용)
    @GetMapping("/leaveListMng")
    public String leaveListMng(Model model) {
        System.out.println("연차 관리자용");
        LeaveDTO dto = service.mngLeaveList(); 
        System.out.println("/attend/leave/leaveListMng : "+dto);
        
        model.addAttribute("mainUrl", "attend/leave/leaveListMng");
        model.addAttribute("listMngData", dto);
        return "home"; 
    }
    
}
