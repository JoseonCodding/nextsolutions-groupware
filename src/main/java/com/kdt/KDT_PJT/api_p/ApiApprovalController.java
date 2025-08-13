package com.kdt.KDT_PJT.api_p;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiApprovalController {
	
	@Autowired
	ApprovalMapper approvalMapper;
	
	@GetMapping("approval")
	Object schedules(HttpSession session) {
		
		EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
		String type = null;
		String status = null;
		int page = 1;
		int size = 5;	// 페이지 당 표시될 게시글 개수
    	int offset = (page - 1) * size;	// 페이지 마다 표시되는 게시글의 시작점 (ex.1페이지:0~9번, 2페이지:10~19번...)
    	int totalCount = approvalMapper.approvalCountAll(type, status);	// 게시글 DB 전체 개수
    	int totalPages = (int) Math.ceil((double) totalCount / size);	// 전체 페이지 수 ('전체 게시글÷페이지당 게시글 수'를 '올림' 처리) 
    	int blockSize = 5;	// 페이지네이션에 나타낼 페이지 개수
    	int startPage, endPage;
    	
    	startPage = Math.max(1,  page - 2);	// 페이지네이션에 나타낼 페이지의 첫 페이지 값
    	endPage = Math.min(totalPages, startPage + blockSize - 1);	// 페이지네이션에 나타낼 페이지의 끝 페이지 값
		
    	if ((endPage - startPage + 1) < blockSize && (endPage == totalPages || startPage == 1)) {
    	    startPage = Math.max(1, endPage - blockSize + 1);
    	}
    	
    	// 필터에 해당하는 게시글이 없는 경우 endPage=0이 되는 상황 방지 
    	if (totalPages == 0) {endPage = 1;}
    	
    	List<ApprovalDTO> approvalData = approvalMapper.approvalData(offset, size, type, status);
		
		System.out.println("/api/approval 진입 : "+ approvalData);
		return approvalData;

	}
	
}