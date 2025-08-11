package com.kdt.KDT_PJT.approval.ctl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

@Controller
@RequestMapping("/approval")
public class ApprovalController {
	
	// 콘텐츠 영역 상단 헤더용
	@ModelAttribute("navUrl")
	public String navUrl() {
		
		return "approval/approvalNav";
	}
	
	@Autowired
	ApprovalMapper approvalMapper;
    
	@RequestMapping("/main")
	public String approvalMain(
						Model model, // 데이터 보관용 (컨트롤러>모델>뷰)
						@RequestParam(name = "page", defaultValue = "1") int page, // 현재 페이지 번호
						@RequestParam(name = "type", required = false) String type,
						@RequestParam(name = "status", required = false) String status) {
		
		int size = 10;	// 페이지 당 표시될 게시글 개수
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
    	
    	ApprovalDTO approvalData = approvalMapper.view(docId);
    
    	model.addAttribute("approvalData", approvalData);

    	model.addAttribute("page", page);
    	model.addAttribute("type", type);
    	model.addAttribute("status", status);
    	
    	model.addAttribute("mainUrl", "approval/approvalViewer");
    	return "navTap";
    }
    
    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(
        @RequestParam("fileName") String fileName,
        @RequestParam("orgName") String orgName
    ) throws UnsupportedEncodingException {
        String path = "C:/upload/" + fileName;
        Resource resource = new FileSystemResource(path); // ★ 좌변 Resource!
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        return ResponseEntity.ok().headers(headers).body(resource);
    }


    @RequestMapping("/delete")
    public String approvalDelete (
    					RedirectAttributes redirectAttributes,
				        @RequestParam("docId") String docId,
				        @RequestParam(name = "page", defaultValue = "1") int page,
				        @RequestParam(name = "type", required = false) String type,
				        @RequestParam(name = "status", required = false) String status,
				        @RequestParam(name = "docType") String docType) {
    	
        if ("공지사항".equals(docType)) {
            approvalMapper.deleteNotice(docId);
        } else if ("연차".equals(docType)) {
            approvalMapper.deleteLeave(docId);
        } else if ("프로젝트".equals(docType)) {
            approvalMapper.deleteProject(docId);
        }
        
        int totalCount = approvalMapper.approvalCountAll(type, status);	// 게시글 DB 전체 개수 (필터기능 반영됨)
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
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "docType") String docType) {
        
    	String rawContent = editData.getContent();

        Safelist customSafelist = Safelist.basicWithImages()
        		// 테이블 관련 태그 허용
        	    .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
        	    // 테이블 관련 속성 허용
        	    .addAttributes("table", "style", "border", "cellpadding", "cellspacing", "width", "height")
        	    .addAttributes("th", "style", "colspan", "rowspan", "width", "height")
        	    .addAttributes("td", "style", "colspan", "rowspan", "width", "height")
        	    .addAttributes("tr", "style")
        	    .addAttributes("thead", "style")
        	    .addAttributes("tbody", "style")
        	    .addAttributes("tfoot", "style")
        	    .addAttributes("col", "style", "span", "width")
        	    .addAttributes("colgroup", "span", "width", "style")
        	    .addAttributes("caption", "style")
        	    // 이미지 태그 및 속성 허용
        	    .addAttributes("img", "style", "src", "alt", "width", "height")
        	    .addProtocols("img", "src", "data", "http", "https")
        	    // 링크 태그와 속성 허용
        	    .addTags("a")
        	    .addAttributes("a", "href", "title", "target", "rel")
        	    .addProtocols("a", "href", "http", "https", "mailto")
        	    // 스타일 태그 허용 및 주요 CSS 스타일 허용
        	    .addAttributes(":all", "style")
        	;

        String safeContent = Jsoup.clean(rawContent, customSafelist);

        editData.setContent(safeContent);

        if ("공지사항".equals(docType)) {
            approvalMapper.editNotice(editData);
        } else if ("연차".equals(docType)) {
            approvalMapper.editLeave(editData);
        } else if ("프로젝트".equals(docType)) {
            approvalMapper.editProject(editData);
        }

        redirectAttributes.addAttribute("docId", editData.getDocId());
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);

        return "redirect:/approval/viewer";
    }
    
    @PostMapping("/approve")
    public String approvalApprove(
        RedirectAttributes redirectAttributes,
        @RequestParam("docId") String docId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "docType") String docType) {

        if ("공지사항".equals(docType)) {
            approvalMapper.updateStatusNotice(docId, "완료");
        } else if ("연차".equals(docType)) {
            approvalMapper.updateStatusLeave(docId, "완료");
        } else if ("프로젝트".equals(docType)) {
            approvalMapper.updateStatusProject(docId, "완료");
        }

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
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "docType") String docType) {

        if ("공지사항".equals(docType)) {
            approvalMapper.updateStatusNotice(docId, "반려");
        } else if ("연차".equals(docType)) {
            approvalMapper.updateStatusLeave(docId, "반려");
        } else if ("프로젝트".equals(docType)) {
            approvalMapper.updateStatusProject(docId, "반려");
        }

        redirectAttributes.addAttribute("docId", docId);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);

        return "redirect:/approval/viewer";
    }


}
