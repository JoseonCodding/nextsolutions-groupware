package com.kdt.KDT_PJT.document.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.document.mapper.DocumentMapper;
import com.kdt.KDT_PJT.document.model.DocumentDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentMapper documentMapper;

    /** 문서관리 메인: gid별 최신 버전 목록 (상태: 진행중/완료) */
    @GetMapping("/main")
    public String documentMain(Model model,
                               HttpSession session,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String employeeId = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(employeeId);

        int limit  = Math.max(1, size);
        int offset = (page - 1) * limit;

        List<DocumentDTO> list = documentMapper.findDocsForManage(employeeId, isAdmin, limit, offset);
        int total = documentMapper.countDocsForManage(employeeId, isAdmin);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) limit));
        
        page = Math.min(Math.max(1, page), totalPages);
        

        // 👇 페이지 버튼 묶음 계산 (자유게시판과 동일)
        int win = 5;
        int startPage = ((page - 1) / win) * win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);


        model.addAttribute("approvalData", list);  // 기존 뷰 키 그대로 사용
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("mainUrl", "document/document_list");
        return "home";
    }

    /** 문서 상세보기: ver 없으면 해당 gid의 '진행중/완료' 최신버전 */
    @GetMapping("/detail")
    public String documentDetail(@RequestParam("gid") String gid,
                                 @RequestParam(value = "ver", required = false) Integer ver,
                                 @RequestParam(value="page", defaultValue="1") int page,
                                 @RequestParam(value="size", defaultValue="10") int size,
                                 @RequestParam(value="keyword", required=false) String keyword,
                                 Model model,
                                 HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        DocumentDTO doc = (ver != null)
                ? documentMapper.findByGidAndVer(gid, ver)
                : documentMapper.findLatestApprovedByGid(gid); // 진행중/완료 최신 1건

        if (doc == null) return "redirect:/document/main";
        if (!isAdmin && !me.equals(doc.getEmployeeId())) return "redirect:/document/main";

        model.addAttribute("doc", doc);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("mainUrl", "document/document_detail");
        return "home";
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
        return "home";
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
        return "home";
    }

    /** 관리자 사번 판별 (업무 규칙 반영) */
    private boolean isAdmin(String employeeId) {
        return "20250001".equals(employeeId)   // 대표
            || "20250005".equals(employeeId); // 문서 관리자
    }
}
