package com.kdt.KDT_PJT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attend/leave")
public class LeaveController {

	//연차 관리(사용자용)
    @GetMapping
    public String leave() {
        System.out.println("연차 관리 페이지");
        return "attend/leave/leave"; 
    }
    
    //연차 사용 신청(사용자용)
    @RequestMapping("/insert")
    public String insert() {
    	System.out.println("연차 신청 페이지");
        return "attend/leave/insert"; 
    }
}
