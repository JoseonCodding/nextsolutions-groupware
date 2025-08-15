
//package com.kdt.KDT_PJT.document.controller;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.kdt.KDT_PJT.document.mapper.DocumentMapper;
//import com.kdt.KDT_PJT.document.model.DocumentDTO;
//
//@Controller
//@RequestMapping("/document")
//public class DocumentController {
//
//    private final DocumentMapper documentMapper;
//
//    public DocumentController(DocumentMapper documentMapper) {
//        this.documentMapper = documentMapper;
//    }
//
//
//	@RequestMapping("/main") public String documentMain(Model model) {
//	List<DocumentDTO> list = documentMapper.selectAll();
//	model.addAttribute("approvalData", list); model.addAttribute("mainUrl",
//	"document/document_list"); return "home"; }
//
//
//
//	@RequestMapping("/viewer") public String
//	documentViewer(@RequestParam("versionId") Long versionId, Model model) {
//	DocumentDTO dto = documentMapper.selectByVersionId(versionId);
//	model.addAttribute("doc", dto); model.addAttribute("mainUrl",
//	"document/documentViewer"); return "navTap"; }
//
//
//    @GetMapping("/downloadFile")
//    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
//                                                 @RequestParam("orgName") String orgName) {
//        try {
//            String path = "C:/upload/" + fileName;
//            Resource resource = new FileSystemResource(path);
//
//            if (!resource.exists()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
//            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
//            headers.add("Content-Transfer-Encoding", "binary");
//
//            return ResponseEntity.ok().headers(headers).body(resource);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//
//
//}

package com.kdt.KDT_PJT.document.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    /** 문서관리 메인: '완료' 상태의 최신 버전 목록 */
    @GetMapping("/main")
    public String documentMain(Model model,
                               HttpSession session,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "20") int size) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String employeeId = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(employeeId);

        int limit = Math.max(1, size);
        int offset = Math.max(0, (Math.max(1, page) - 1) * limit);

        List<DocumentDTO> list = documentMapper.findDocsForManage(employeeId, isAdmin, limit, offset);
        int total = documentMapper.countDocsForManage(employeeId, isAdmin); // Mapper에 구현되어 있어야 함
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) limit));

        model.addAttribute("approvalData", list);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("mainUrl", "document/document_list");
        return "home";
    }

    /** 문서 상세보기: ver 없으면 '완료' 최신버전으로 */
    @GetMapping("/detail")
    public String documentDetail(@RequestParam(name = "gid") String gid,
                                 @RequestParam(name = "ver", required = false) BigDecimal ver,
                                 Model model,
                                 HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        DocumentDTO doc = (ver != null)
                ? documentMapper.findByGidAndVer(gid, ver)
                : documentMapper.findLatestApprovedByGid(gid); // Mapper에 구현되어 있어야 함

        if (doc == null) return "redirect:/document/main";
        if (!isAdmin && !me.equals(doc.getEmployeeId())) return "redirect:/document/main";

        model.addAttribute("doc", doc);
        model.addAttribute("mainUrl", "document/document_detail");
        return "home";
    }

    /** 버전 목록 보기: 본인/관리자만 */
    @GetMapping("/versions")
    public String versionList(@RequestParam(name = "gid") String gid, Model model, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String me = loginUser.getEmployeeId();
        boolean isAdmin = isAdmin(me);

        // 소유자 검증을 위해 대표 한 건 로딩(없으면 목록 비움 처리)
        DocumentDTO latest = documentMapper.findLatestApprovedByGid(gid);
        if (latest != null && !isAdmin && !me.equals(latest.getEmployeeId())) return "redirect:/document/main";

        List<DocumentDTO> versions = documentMapper.findVersions(gid);
        boolean allowed = isAdmin(me) || versions.stream()
            .anyMatch(v -> me.equals(v.getEmployeeId()));
        if (!allowed) return "redirect:/document/main";

        model.addAttribute("gid", gid);
        model.addAttribute("versions", versions);
        model.addAttribute("mainUrl", "document/document_versionlist");
        return "home";
    }

    /** 버전 목록 상세 (직접 ver 지정) */
    @GetMapping("/version")
    public String versionView(@RequestParam(name = "gid") String gid,
                              @RequestParam(name = "ver") BigDecimal ver,
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

    /** 파일 다운로드 (path는 저장경로/키, fileName은 표시명) */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam(name = "path") String path,
                                             @RequestParam(name = "fileName") String fileName) {
        FileSystemResource resource = new FileSystemResource(path);
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String encoded = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }

    /** 관리자 사번 판별 */
    private boolean isAdmin(String employeeId) {
        return "20250005".equals(employeeId) || "20250001".equals(employeeId);
    }
}

