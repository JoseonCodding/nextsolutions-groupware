package com.kdt.KDT_PJT.approval.ctl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

import jakarta.annotation.Resource;


@Controller
@RequestMapping("/approval")
public class ApprovalController {
	
	// 콘텐츠 영역 상단 헤더용
	@ModelAttribute("navUrl")
	public String navUrl() {
		
		return "approval/approvalNav";
	}
	
	@Resource
	ApprovalMapper approvalMapper;
    
	@RequestMapping("/main")
	public String approvalMain(
						Model model, // 데이터 보관용 (컨트롤러>모델>뷰)
						@RequestParam(name = "page", defaultValue = "1") int page, // 현재 페이지 번호
						@RequestParam(name = "type", required = false) String type,
						@RequestParam(name = "status", required = false) String status) {
		
		int size = 10;	// 페이지 당 표시될 게시글 개수
    	int offset = (page - 1) * size;	// 페이지 마다 표시되는 게시글의 시작점 (ex.1페이지:0~9번, 2페이지:10~19번...)
    	int totalCount = approvalMapper.noticeCountAll(type, status);	// 게시글 DB 전체 개수
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
    	
    	List<ApprovalDTO> noticeData = approvalMapper.noticeData(offset, size, type, status);	// 공지사항 DB
    	List<ApprovalDTO> leaveData = approvalMapper.leaveData(offset, size, type, status);		// 연차 DB
    	List<ApprovalDTO> projectData = approvalMapper.projectData(offset, size, type, status);		// 프로젝트 DB

    	// 공지사항 DB + 연차 DB 합치기
    	List<ApprovalDTO> approvalData = new ArrayList<>();
    	approvalData.addAll(noticeData);
    	approvalData.addAll(leaveData);
    	approvalData.addAll(projectData);

    	approvalData.sort(Comparator.comparing(ApprovalDTO::getCreatedAt).reversed());	// 생성일 내림차순 정렬(옵션)

    	model.addAttribute("approvalData", approvalData);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("totalPages", totalPages);
    	model.addAttribute("startPage", startPage);
    	model.addAttribute("endPage", endPage);
    	
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalMain");
    	return "navTap";
    }
	
    @RequestMapping("/viewer")
    public String approvalViewer(
			    		Model model,
			    		@RequestParam("docId") String docId,
			            @RequestParam(name = "page", defaultValue = "1") int page,
			            @RequestParam(name = "type", required = false) String type,
			            @RequestParam(name = "status", required = false) String status) {
    	
    	ApprovalDTO viewData = approvalMapper.view(docId);
    
    	model.addAttribute("viewData", viewData);

    	model.addAttribute("page", page);
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalViewer");
    	return "navTap";
    }

    @RequestMapping("/delete")
    public String approvalDelete (
    					RedirectAttributes redirectAttributes,
				        @RequestParam("docId") String docId,
				        @RequestParam(name = "page", defaultValue = "1") int page,
				        @RequestParam(name = "type", required = false) String type,
				        @RequestParam(name = "status", required = false) String status) {
    	
        approvalMapper.delete(docId);
        
        int totalCount = approvalMapper.noticeCountAll(type, status);	// 게시글 DB 전체 개수 (필터기능 반영됨)
        int size = 10;	// 페이지 당 표시될 게시글 수
        int totalPages = (int) Math.ceil((double) totalCount / size);	// 전체 페이지 수 ('전체 게시글÷페이지당 게시글 수'를 '올림' 처리) 
        
        int deletePage = page > totalPages ? totalPages : page; // 현재 페이지가 마지막 페이지보다 크면 마지막 페이지로 이동
        if (totalPages == 0) {deletePage = 1;}	// 필터에 해당하는 게시글이 없는 경우 endPage=0이 되는 상황 방지
        
        redirectAttributes.addAttribute("page", deletePage);
        redirectAttributes.addAttribute("type", type == null ? "" : type);
        redirectAttributes.addAttribute("status", status == null ? "" : status);

        return "redirect:/approval/main";
    }
    
    @GetMapping("/edit")
    public String approvalEditForm(
    					Model model,
				        @RequestParam("docId") String docId,
				        @RequestParam(name = "page", defaultValue = "1") int page,
				        @RequestParam(name = "type", required = false) String type,
				        @RequestParam(name = "status", required = false) String status) {
    	
    	ApprovalDTO editData = approvalMapper.view(docId);
    	
    	model.addAttribute("editData", editData);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalEditForm");
    	return "navTap";
    }
    
    @PostMapping("/edit")
    public String approvalEditProc(
    				RedirectAttributes redirectAttributes,
					@ModelAttribute ApprovalDTO editData,
					@RequestParam(name = "page", defaultValue = "1") int page,
					@RequestParam(name = "type", required = false) String type,
					@RequestParam(name = "status", required = false) String status) {
    	
    	approvalMapper.edit(editData);
        
    	redirectAttributes.addAttribute("docId", editData.getDocId());
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);
        
        return "redirect:/approval/viewer";	// 파라미터가 쿼리스트링에 자동으로 붙음
    }
    
    @PostMapping("/approve")
    public String approvalApprove(
        RedirectAttributes redirectAttributes,
        @RequestParam("docId") String docId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "status", required = false) String status) {

        approvalMapper.updateStatus(docId, "완료");

        redirectAttributes.addAttribute("docId", docId);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);

        return "redirect:/approval/viewer";
    }

    @PostMapping("/reject")
    public String approvalReject(
        RedirectAttributes redirectAttributes,
        @RequestParam("docId") String docId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "status", required = false) String status) {

        approvalMapper.updateStatus(docId, "반려");

        redirectAttributes.addAttribute("docId", docId);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);

        return "redirect:/approval/viewer";
    }


}
