package com.kdt.KDT_PJT.approval.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String approvalMain(
			Model model, // 데이터 보관용 (컨트롤러>모델>뷰)
						 @RequestParam(value = "page", defaultValue = "1") int page, // 페이지네이션용 파라미터 (페이지 번호, 사이즈)
						 @RequestParam(value = "size", defaultValue = "10") int size) {
    	
    	// 데이터베이스 값 모델에 집어넣기
    	model.addAttribute("approvalData", approvalMapper.selectAllDesc());

    	System.out.println("	★게시글 DB 모든 내용" + approvalMapper.selectAllDesc());	// 게시글 DB Console 확인용
    	
    	// 페이지네이션	
    	System.out.println("	★게시글 DB 전체 개수 : " + approvalMapper.countAll());

    	return "approval/approvalMain";
    }
	

}
