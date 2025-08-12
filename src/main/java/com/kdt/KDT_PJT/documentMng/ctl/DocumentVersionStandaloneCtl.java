package com.kdt.KDT_PJT.documentMng.ctl;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.documentMng.model.DocumentMngDTO;
import com.kdt.KDT_PJT.documentMng.model.DocumentVersionSummaryDTO;
import com.kdt.KDT_PJT.documentMng.svc.DocumentMngService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/documentMng/standalone")
@RequiredArgsConstructor
public class DocumentVersionStandaloneCtl {

    private final DocumentMngService service;

    // ✅ 버전 목록 페이지 (독립)
    @GetMapping("/versions")
    public String versionListStandalone(@RequestParam("originalId") Long originalId, Model model) {
        List<DocumentVersionSummaryDTO> list = service.getVersionList(originalId);
        model.addAttribute("versionList", list);
        model.addAttribute("originalId", originalId);
        return "documentMng/versionListStandalone"; // navTap 거치지 않음
    }

    // ✅ 버전 상세 페이지 (독립 뷰어)
    @GetMapping("/versionDetail")
    public String versionDetailStandalone(@RequestParam("versionId") Long versionId, Model model) {
        DocumentMngDTO dto = service.getVersionDetail(versionId);
        model.addAttribute("doc", dto);
        return "documentMng/versionDetailStandalone"; // navTap 거치지 않음
    }
    
    
	/*
	 * // 작업중입니다 (필규) // ✅ 복원 처리
	 * 
	 * @PostMapping("/restoreVersion") public String
	 * restoreVersionStandalone(@RequestParam("versionId") Long versionId,
	 * 
	 * @RequestParam("originalId") Long originalId) {
	 * service.restoreVersion(versionId); return
	 * "redirect:/documentMng/standalone/versions?originalId=" + originalId +
	 * "&success=true"; }
	 */

}
