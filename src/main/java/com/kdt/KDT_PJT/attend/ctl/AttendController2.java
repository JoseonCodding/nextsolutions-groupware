package com.kdt.KDT_PJT.attend.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attend")
public class AttendController2 {
	
    //근태 관리자 페이지(출퇴근 변경이력 포함)
    @PostMapping("/save")
    public String attendSave(Model model) {
    	
        return "redirect:/attend"; 
    }

}
