package com.kdt.KDT_PJT.attend.ctl;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.attend.model.LeaveReqDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/attend")
public class LeaveController {
	
	@ModelAttribute("navUrl")
	String navUrl() {
		return "attend/nav";
	}

	
	@Resource
	LeaveMapper mapper;
	
	
	//연차 관리(사용자용)
    @GetMapping("/leaveList")
    public String leaveList(HttpSession session,Model model) {
    	
    	EmployeeDto me =(EmployeeDto)session.getAttribute("me");
    	
    	// 주석처리요망 -->
    	//me = new EmployeeDto();
    	//me.setEmployeeId("20250001");
        System.out.println("연차 관리 페이지");
        
        LeaveDTO dto = mapper.getAnnualLeaveOne(me); 
        System.out.println("/attend/leave : "+dto);
        // 홈에서 뜨는 화면 연결
        model.addAttribute("mainUrl", "attend/leave/leaveList");
        
        model.addAttribute("listData", dto);
        
        
        return "navTap"; 
    }
    
    //연차 사용 신청(사용자용)
    @GetMapping("/insert")
    public String insert(HttpSession session,Model model) {
    	System.out.println("연차 신청 페이지");
    	
    	EmployeeDto me =(EmployeeDto)session.getAttribute("me");
    	
    	// 주석처리요망 -->
    	//me = new EmployeeDto();
    	//me.setEmployeeId("20250001");
    	//  <--
    	
    	List<LeaveDTO> restData = mapper.annualLeaveRest(me); 
    	
    	model.addAttribute("restData", restData);
    	
    	model.addAttribute("mainUrl", "attend/leave/insert");
        return "navTap"; 
    }
 
    //연차 사용 신청(사용자용) 정보 보내기
    //Url 수정 완료하기
    @PostMapping("/insert")
    public String insertReg(HttpSession session, Model model, LeaveReqDTO reqDto) {
    	
    	
    	EmployeeDto me =(EmployeeDto)session.getAttribute("me");
    	
    	// 주석처리요망 -->
    	//me = new EmployeeDto();
    	//me.setEmployeeId("20250001");
    	//  <--
    	
    	reqDto.dataCalc();
    	System.out.println("연차 신청 페이지 : "+reqDto.getArr().size());
    	
    	for (LeaveDTO dto : reqDto.getArr()) {
    		mapper.approvalList(dto); 
		}
    	
    	
    	model.addAttribute("mainUrl", "attend/leave/leaveList");
        return "navTap"; 
    }

    //연차 관리(관리자용)
    @GetMapping("/leaveListMng")
    public String leaveListMng(HttpSession session, Model model) {
        System.out.println("연차 관리자용");
        
        EmployeeDto me =(EmployeeDto)session.getAttribute("me");
        
        List<LeaveDTO> dto = mapper.mngLeaveList(); 
        
    
        System.out.println("/attend/leave/leaveListMng : "+dto);
        
        model.addAttribute("mainUrl", "attend/leave/leaveListMng");
        model.addAttribute("listMngData", dto);
        return "navTap"; 
    }

}
