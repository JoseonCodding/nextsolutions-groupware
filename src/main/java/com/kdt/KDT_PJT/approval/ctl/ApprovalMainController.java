package com.kdt.KDT_PJT.approval.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;

@Controller
@RequestMapping("/approval")
public class ApprovalMainController {
	
	// 생성자 주입 방식 컨트롤러 (@Autowired 생략)
    private final ApprovalMapper approvalMapper;
    
    // 생성자에서 주입받기
    public ApprovalMainController(ApprovalMapper approvalMapper) {
    	this.approvalMapper = approvalMapper;
    }
    
    @RequestMapping("/main")
    public String approvalMain(Model model) {
    	// 1. approvalMapper.selectAll()으로 DB 데이터 읽기
    	model.addAttribute("approvalData", approvalMapper.selectAll());
    	// 2. 뷰 이름 반환
    	
    	System.out.println(approvalMapper.selectAll());
    	
    	return "approval/approvalMain";
    }
	

}
