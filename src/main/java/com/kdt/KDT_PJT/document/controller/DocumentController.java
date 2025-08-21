package com.kdt.KDT_PJT.document.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.document.mapper.DocumentMapper;
import com.kdt.KDT_PJT.document.model.DocumentDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentMapper documentMapper;
    
    @ModelAttribute("navUrl")
	String navUrl() {
		return "document/documentNav";
	}

    /** 문서관리 메인: gid별 최신 버전 목록 (상태: 진행중/완료) */
    @GetMapping("/main")
    public String documentMain(Model model,
                               HttpSession session,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keywordType", required = false) String keywordType,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "sort", defaultValue = "newest") String sort) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        int pageSafe = Math.max(1, page);
        int sizeSafe = Math.min(Math.max(1, size), 100);
        int offset = (pageSafe - 1) * sizeSafe;

        // 검색/정렬 화이트리스트
        String typeKey = ("writer".equalsIgnoreCase(keywordType) ||
                          "project".equalsIgnoreCase(keywordType) ||
                          "status".equalsIgnoreCase(keywordType)) ? keywordType.toLowerCase() : null;
        String kw = (keyword == null) ? null : keyword.trim();
        boolean hasSearch = (typeKey != null && kw != null && !kw.isEmpty());
        String sortKey = "newest"; // 확장 여지

        // 데이터 조회
        List<DocumentDTO> rows = hasSearch
                ? documentMapper.findDocsForManageWithSearch(me, isAdmin, typeKey, kw, sortKey, sizeSafe, offset)
                : documentMapper.findDocsForManage(me, isAdmin, sizeSafe, offset);
        int total = hasSearch
                ? documentMapper.countDocsForManageWithSearch(me, isAdmin, typeKey, kw)
                : documentMapper.countDocsForManage(me, isAdmin);

        int totalPages = Math.max(1, (int) Math.ceil(total / (double) sizeSafe));
        pageSafe = Math.min(pageSafe, totalPages);

        // 페이지 버튼(묶음 5개)
        int win = 5;
        int startPage = ((pageSafe - 1) / win) * win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        // 모델
        model.addAttribute("approvalData", rows);
        model.addAttribute("page", pageSafe);
        model.addAttribute("size", sizeSafe);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 폼 상태 유지
        model.addAttribute("keywordType", typeKey);
        model.addAttribute("keyword", kw);
        model.addAttribute("sort", sortKey);

        model.addAttribute("mainUrl", "document/document_list");
        return "navTap";
    }

    /** 문서 상세보기: ver 없으면 해당 gid의 '진행중/완료' 최신버전 */
    @GetMapping("/detail")
    public String documentDetail(@RequestParam(name = "gid") String gid,
            					 @RequestParam(name = "ver", required = false) Integer ver,
            					 @RequestParam(name = "page", defaultValue = "1") int page,
            					 @RequestParam(name = "size", defaultValue = "10") int size,
            					 @RequestParam(name = "keywordType", required = false) String keywordType,
            					 @RequestParam(name = "keyword", required = false) String keyword,
            					 @RequestParam(name = "sort", defaultValue = "newest") String sort,
                                 Model model, HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        DocumentDTO doc = (ver != null)
                ? documentMapper.findByGidAndVer(gid, ver)
                : documentMapper.findLatestApprovedByGid(gid); // 진행중/완료 최신 1건
        if (doc == null) return "redirect:/document/main";
     // 권한: 관리자 또는 소유자만
        if (!isAdmin && !me.equals(doc.getEmployeeId())) return "redirect:/document/main";

        model.addAttribute("doc", doc);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("keywordType", keywordType);
        model.addAttribute("sort", sort);
        model.addAttribute("mainUrl", "document/document_detail");
        return "navTap";
    }

    /** 버전 목록: 같은 gid의 모든 버전 (소유자 또는 관리자만 접근) */
    @GetMapping("/versions")
    public String versionList(@RequestParam("gid") String gid, Model model, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        // 최신 문서 기준 빠른 권한 체크
        DocumentDTO latest = documentMapper.findLatestApprovedByGid(gid);
        if (latest != null && !isAdmin && !me.equals(latest.getEmployeeId())) {
            // 과거 버전에 내 소유가 있을 수 있으므로 전체 확인
            List<DocumentDTO> versionsTmp = documentMapper.findVersions(gid);
            boolean allowedTmp = versionsTmp.stream().anyMatch(v -> me.equals(v.getEmployeeId()));
            if (!allowedTmp) return "redirect:/document/main";
        }

        List<DocumentDTO> versions = documentMapper.findVersions(gid);
        boolean allowed = isAdmin || versions.stream().anyMatch(v -> me.equals(v.getEmployeeId()));
        if (!allowed) return "redirect:/document/main";

        model.addAttribute("gid", gid);
        model.addAttribute("versions", versions);
        model.addAttribute("mainUrl", "document/document_versionlist");
        return "navTap";
    }

    /** 버전 상세: 지정 ver 1건 (소유자 또는 관리자만 접근) */
    @GetMapping("/version")
    public String versionView(@RequestParam("gid") String gid,
                              @RequestParam("ver") Integer ver,
                              Model model,
                              HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        DocumentDTO doc = documentMapper.findByGidAndVer(gid, ver);
        if (doc == null) return "redirect:/document/main";
        if (!isAdmin && !me.equals(doc.getEmployeeId())) return "redirect:/document/main";

        model.addAttribute("doc", doc);
        model.addAttribute("mainUrl", "document/version_detail");
        return "navTap";
    }
    
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
        @RequestParam("fileName") String fileName,
        @RequestParam("orgName") String orgName,
        HttpServletRequest request
    ) throws Exception { 
    	//파일 저장
    			//String dirPath = request.getServletContext().getRealPath("fff")+"\\";
    			
    			String dirPath = "C:/upload/";
    			Path fPath = Paths.get(dirPath+fileName).normalize();
    			
    			Resource source = new UrlResource(fPath.toUri());
    			
    			return ResponseEntity
    					.ok()
    					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName)
    					.contentType(MediaType.APPLICATION_OCTET_STREAM)
    					.body(source);
    }

    /** 관리자 사번 판별 (업무 규칙 반영) */
    private boolean isAdmin(String employeeId) {
        return "20250001".equals(employeeId)   // 대표
            || "20250005".equals(employeeId); // 문서 관리자
    }
}
