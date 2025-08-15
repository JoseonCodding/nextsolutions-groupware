package com.kdt.KDT_PJT.approval.ctl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	@Autowired
	LeaveMapper leaveMapper;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
    
	@RequestMapping("/main")
	public String approvalMain(
	        Model model,
	        HttpServletRequest request,
	        @RequestParam(name = "page", defaultValue = "1") int page,
	        @RequestParam(name = "type", required = false) String type,
	        @RequestParam(name = "status", required = false) String status) {

	    HttpSession session = request.getSession(false);
	    EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

	    List<ApprovalDTO> approvalData;
	    int totalPages;
	    int startPage;
	    int endPage;
	    int size = 10;

	    if (loginUser == null) {
	        approvalData = List.of();
	        totalPages = 0;
	        startPage = 1;
	        endPage = 1;
	    } else {
	        String role = loginUser.getRole();
	        String employeeId = loginUser.getEmployeeId();
	        int offset = (page - 1) * size;

	        // count
	        int totalCount = approvalMapper.approvalCountByRole(role, type, status, employeeId);
	        totalPages = (int) Math.ceil((double) totalCount / size);

	        startPage = Math.max(1, page - 2);
	        endPage = Math.min(totalPages, startPage + 4);
	        if ((endPage - startPage + 1) < 5 && (endPage == totalPages || startPage == 1)) {
	            startPage = Math.max(1, endPage - 4);
	        }
	        if (totalPages == 0) endPage = 1;

	        approvalData = approvalMapper.approvalDataByRole(offset, size, role, type, status, employeeId);
	    }

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
	    RedirectAttributes redirectAttributes,
	    HttpServletRequest request,
	    @RequestParam("docId") String docId,
	    @RequestParam(name = "page", defaultValue = "1") int page,
	    @RequestParam(name = "type", required = false) String type,
	    @RequestParam(name = "status", required = false) String status) {

	    HttpSession session = request.getSession(false);
	    EmployeeDto loginUser = (session != null) ? 
	        (EmployeeDto) session.getAttribute("loginUser") : null;

	    // 1. 로그인 여부 체크
	    if (loginUser == null) {
	        return "redirect:/login?error=auth";
	    }

	    // 2. 권한 필터 내장된 view() 호출
	    ApprovalDTO approvalData = approvalMapper.view(
	        docId,
	        loginUser.getRole(),
	        loginUser.getEmployeeId(),
	        type,
	        status
	    );

	    // 3. 조회 결과 없으면 접근 차단 (권한 없음 or 없는 문서)
	    if (approvalData == null) {
	        return "redirect:/approval/main?error=forbidden";
	    }

	    // 4. 정상 데이터 모델에 담기
	    model.addAttribute("loginUser", loginUser);
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
    public String approvalDelete(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("docId") String docId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ?
            (EmployeeDto) session.getAttribute("loginUser") : null;

        if (loginUser == null) {
            return "redirect:/login?error=auth";
        }

        // 권한 필터 포함 단건 조회
        ApprovalDTO doc = approvalMapper.view(
            docId, loginUser.getRole(), loginUser.getEmployeeId(), type, status
        );

        if (doc == null) {
            return "redirect:/approval/main?error=forbidden"; // 문서 없음 or 권한 없음
        }

        // 작성자 본인만 삭제 가능
        if (!loginUser.getEmployeeId().equals(doc.getWriterId())) {
            return "redirect:/approval/main?error=forbidden";
        }

        String pkId = docId.split("_")[1];

        // 공지사항만 소프트 삭제 실행
        if ("공지사항".equals(doc.getDocType())) {
            approvalMapper.softDeleteNotice(pkId);
        } else {
            return "redirect:/approval/main?error=deleteNotAllowed";
        }

        // 삭제 이후 페이지 재계산 (role, employeeId 포함)
        int size = 10;
        int totalCount = approvalMapper.approvalCountByRole(
            loginUser.getRole(), type, status, loginUser.getEmployeeId()
        );
        int totalPages = (int) Math.ceil((double) totalCount / size);
        int deletePage = page > totalPages ? totalPages : page;
        if (totalPages == 0) deletePage = 1;

        redirectAttributes.addAttribute("page", deletePage);
        redirectAttributes.addAttribute("type", type == null ? "" : type);
        redirectAttributes.addAttribute("status", status == null ? "" : status);

        return "redirect:/approval/main";
    }

    
    @GetMapping("/edit")
    public String approvalEditForm(
            Model model,
            HttpServletRequest request,
            @RequestParam("docId") String docId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;
        if (loginUser == null) return "redirect:/login?error=auth";

        ApprovalDTO editData = approvalMapper.view(
            docId, loginUser.getRole(), loginUser.getEmployeeId(), type, status
        );
        if (editData == null) return "redirect:/approval/main?error=forbidden";

        // 작성자 본인만 수정 폼 접근 허용
        if (!loginUser.getEmployeeId().equals(editData.getWriterId())) {
            return "redirect:/approval/main?error=forbidden";
        }

        model.addAttribute("editData", editData);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("mainUrl", "approval/approvalEditForm");
        return "navTap";
    }



    
    @PostMapping("/edit")
    public String approvalEditProc(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @ModelAttribute ApprovalDTO editData,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "docType") String docType,
            @RequestParam(name = "actions", required = false) List<String> actions) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;
        if (loginUser == null) return "redirect:/login?error=auth";

        ApprovalDTO current = approvalMapper.view(
            editData.getDocId(), loginUser.getRole(), loginUser.getEmployeeId(), type, status
        );
        if (current == null) return "redirect:/approval/main?error=forbidden";

        // 작성자 본인만 수정 허용
        if (!loginUser.getEmployeeId().equals(current.getWriterId())) {
            return "redirect:/approval/main?error=forbidden";
        }

        // 이하 기존 sanitize/가공 및 UPDATE 그대로 유지
        String rawContent = editData.getContent();
        Safelist customSafelist = Safelist.basicWithImages()
                .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
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
                .addTags("a")
                .addAttributes("a", "href", "title", "target", "rel")
                .addProtocols("a", "href", "http", "https", "mailto")
                .addAttributes(":all", "style")
                .addAttributes("img", "style", "src", "alt", "width", "height")
                .addProtocols("img", "src", "data", "http", "https");
        String safeContent = Jsoup.clean(rawContent, customSafelist);
        editData.setContent(safeContent);

        String pkId = editData.getDocId().split("_")[1];

        if ("연차".equals(docType) && editData.getTitle() != null) {
            String prefix = "연차 사용신청 - ";
            if (editData.getTitle().startsWith(prefix)) {
                editData.setTitle(editData.getTitle().substring(prefix.length()));
            }
        }

        if ("근태".equals(docType)) {
            if (actions != null && !actions.isEmpty()) {
                if (actions.contains("IN") && actions.contains("OUT")) {
                    editData.setTimeInout("출퇴근");
                } else if (actions.contains("IN")) {
                    editData.setTimeInout("출근");
                } else if (actions.contains("OUT")) {
                    editData.setTimeInout("퇴근");
                }
            }
        }

        if ("공지사항".equals(docType)) {
            approvalMapper.editNotice(pkId, editData);
        } else if ("연차".equals(docType)) {
            approvalMapper.editLeave(pkId, editData);
        } else if ("프로젝트".equals(docType)) {
            approvalMapper.editProject(pkId, editData);
        } else if ("근태".equals(docType)) {
            approvalMapper.editAttendance(pkId, editData);
        }

        redirectAttributes.addAttribute("docId", editData.getDocId());
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);
        return "redirect:/approval/viewer";
    }

    
    @PostMapping("/approve")
    @Transactional
    public String approvalApprove(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("docId") String docId) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;
        if (loginUser == null) return "redirect:/login?error=auth";

        ApprovalDTO doc = approvalMapper.view(
            docId, loginUser.getRole(), loginUser.getEmployeeId(), null, null
        );
        if (doc == null) return "redirect:/approval/main?error=forbidden";

        String pkId = docId.split("_")[1];
        String docType = doc.getDocType();
        String currentStatus = doc.getStatus(); // 현재 상태
        String role = loginUser.getRole();

        // 1단계: '대기' 상태에서 담당자가 승인하면 '진행중'으로
        if ("대기".equals(currentStatus) && isResponsibleRole(docType, role)) {
            updateStatusCommon(docType, pkId, "진행중", doc.getTimeInout());
        }
        // 2단계: '진행중' 상태에서 대표가 승인하면 '완료'로
        else if ("진행중".equals(currentStatus) && "대표".equals(role)) {
            updateStatusCommon(docType, pkId, "완료", doc.getTimeInout());
        }
        // 그 외는 승인 권한 없음
        else {
            return "redirect:/approval/main?error=forbidden";
        }

        redirectAttributes.addAttribute("docId", docId);
        return "redirect:/approval/viewer";
    }

    /**
     * 문서 타입별 상태 변경 공통 처리
     */
    private void updateStatusCommon(String docType, String pkId, String newStatus, String timeInout) {
        switch (docType) {
            case "공지사항" -> approvalMapper.updateStatusNotice(pkId, newStatus);
            case "연차" -> {
                approvalMapper.updateStatusLeave(pkId, newStatus);
                // 진행중 → 특별 처리 없음 / 완료 → Mapper 조건에 따라 leave_type 변경
                if ("완료".equals(newStatus)) {
                    leaveMapper.insertScheduleRest(pkId);
                }
            }
            case "프로젝트" -> {
                approvalMapper.updateStatusProject(pkId, newStatus);
                if ("진행중".equals(newStatus) || "완료".equals(newStatus)) {
                    approvalMapper.insertProjectSchedule(pkId);
                }
            }
            case "근태" -> approvalMapper.approveAttendance(pkId, newStatus, timeInout);
        }
    }

    /**
     * 문서 타입과 role 매칭 여부
     */
    private boolean isResponsibleRole(String docType, String role) {
        return switch (docType) {
            case "공지사항" -> "게시판".equals(role);
            case "연차" -> "근태".equals(role);
            case "프로젝트" -> "프로젝트".equals(role);
            case "근태" -> "근태".equals(role);
            default -> false;
        };
    }



    @PostMapping("/reject")
    @Transactional
    public String approvalReject(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("docId") String docId) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;
        if (loginUser == null) return "redirect:/login?error=auth";

        ApprovalDTO doc = approvalMapper.view(
            docId, loginUser.getRole(), loginUser.getEmployeeId(), null, null
        );
        if (doc == null) return "redirect:/approval/main?error=forbidden";

        String pkId = docId.split("_")[1];
        String docType = doc.getDocType();
        String currentStatus = doc.getStatus();
        String role = loginUser.getRole();

        // 1단계: '대기' 상태에서 담당자가 반려
        if ("대기".equals(currentStatus) && isResponsibleRole(docType, role)) {
            updateRejectStatus(docType, pkId);
        }
        // 2단계: '진행중' 상태에서 대표가 반려
        else if ("진행중".equals(currentStatus) && "대표".equals(role)) {
            updateRejectStatus(docType, pkId);
        }
        // 그 외는 반려 권한 없음
        else {
            return "redirect:/approval/main?error=forbidden";
        }

        redirectAttributes.addAttribute("docId", docId);
        return "redirect:/approval/viewer";
    }

    /**
     * 반려 상태 변경 공통 처리
     */
    private void updateRejectStatus(String docType, String pkId) {
        switch (docType) {
            case "공지사항" -> approvalMapper.updateStatusNotice(pkId, "반려");
            case "연차" -> approvalMapper.updateStatusLeave(pkId, "반려");
            case "프로젝트" -> approvalMapper.updateStatusProject(pkId, "반려");
            case "근태" -> approvalMapper.rejectAttendance(pkId, "반려");
        }
    }





}
