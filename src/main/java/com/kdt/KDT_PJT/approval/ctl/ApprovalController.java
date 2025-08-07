package com.kdt.KDT_PJT.approval.ctl;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

import jakarta.annotation.Resource;

@Controller
@RequestMapping("/approval")
public class ApprovalController {
	
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
    	
    	// 필터에 해당하는 게시글이 없는 경우 endPage=0이 되는 상황 방지
    	if (totalPages == 0) {endPage = 1;}
    	
    	List<ApprovalDTO> approvalData = approvalMapper.pageData(offset, size,type, status); // 현재 페이지 게시글 DB 정보 (offset부터 size까지)
    	
    	model.addAttribute("approvalData", approvalData);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("totalPages", totalPages);
    	model.addAttribute("size", size);
    	model.addAttribute("startPage", startPage);
    	model.addAttribute("endPage", endPage);
    	
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalMain");
    	return "home";
    }
	
    @RequestMapping("/viewer")
    public String approvalViewer(
			    		Model model,
			    		@RequestParam("docId") String docId,
			            @RequestParam(name = "page", defaultValue = "1") int page,
			            @RequestParam(name = "type", required = false) String type,
			            @RequestParam(name = "status", required = false) String status) {
    	
    	ApprovalDTO viewData = approvalMapper.selectById(docId);
    
    	model.addAttribute("viewData", viewData);

    	// 삭제 버튼에 주입할 파라미터 Model에 저장
    	model.addAttribute("page", page);
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalViewer");
    	return "home";
    }

    @RequestMapping("/delete")
    public String approvalDelete (
				        @RequestParam("docId") String docId,
				        @RequestParam(name = "page", defaultValue = "1") int page,
				        @RequestParam(name = "type", required = false) String type,
				        @RequestParam(name = "status", required = false) String status,
				        RedirectAttributes redirectAttributes) {
    	
        approvalMapper.deleteById(docId);
        
        // 한글 파라미터 주입으로 생기는 에러 처리
        // RedirectAttributes : "리다이렉트" 시, 파라미터/메시지 데이터를 안전하게 전달하는 용도
        //						자동 URL 인코딩(한글/특수문자 깨짐 방지): addAttribute로 넘긴 값은 자동으로 인코딩되어 URL에 붙음.
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type == null ? "" : type);
        redirectAttributes.addAttribute("status", status == null ? "" : status);

        return "redirect:/approval/main";
    }

	

}
