package com.kdt.KDT_PJT.approval.ctl;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

import jakarta.annotation.Resource;

@Controller
@RequestMapping("/approval")
public class ApprovalMainController {
	
	@Resource
    ApprovalMapper approvalMapper;
    
    @RequestMapping("/main")
    public String approvalMain(
						Model model, // 데이터 보관용 (컨트롤러>모델>뷰)
						@RequestParam(name = "page", defaultValue = "1") int page, // 페이지네이션용 파라미터 (page:페이지 번호, size:페이지당 게시글 수)
						@RequestParam(name = "size", defaultValue = "10") int size,
						@RequestParam(name = "type", required = false) String type,
						@RequestParam(name = "status", required = false) String status) {
    	
    	int offset = (page - 1) * size;	// 페이지 마다 표시되는 게시글의 시작점 (ex.1페이지:0~9번, 2페이지:10~19번...)
    	int totalCount = approvalMapper.countAll(type, status);	// 게시글 DB 전체 개수
    	int totalPages = (int) Math.ceil((double) totalCount / size);	// 전체 페이지 수 ('전체 게시글÷페이지당 게시글 수'를 '올림' 처리) 
    	int blockSize = 5;	// 페이지네이션에 나타낼 페이지 개수
    	int startPage, endPage;
    	
    	startPage = Math.max(1,  page - 2);	// 페이지네이션에 나타낼 페이지의 첫 페이지 값
    	endPage = Math.min(totalPages, startPage + blockSize - 1);	// 페이지네이션에 나타낼 페이지의 끝 페이지 값
		
    	if ((endPage - startPage + 1) < blockSize && (endPage == totalPages || startPage == 1)) {
    	    startPage = Math.max(1, endPage - blockSize + 1);
    	}
    	
    	List<ApprovalDTO> approvalData = approvalMapper.pageData(offset, size,type, status); // 현재 페이지 게시글 DB 정보 (offset부터 size까지)
    	
    	model.addAttribute("approvalData", approvalData);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("totalPages", totalPages);
    	model.addAttribute("size", size);
    	model.addAttribute("startPage", startPage);
    	model.addAttribute("endPage", endPage);
    	
    	return "approval/approvalMain";
    }
	

}
