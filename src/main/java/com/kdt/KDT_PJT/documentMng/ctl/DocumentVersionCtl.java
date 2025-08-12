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
@RequestMapping("/documentMng")
@RequiredArgsConstructor
public class DocumentVersionCtl {

    private final DocumentMngService service;

    // 버전 목록 페이지
    @GetMapping("/versionsStandalone")
    public String versionListStandalone(@RequestParam("originalId") Long originalId, Model model) {
        model.addAttribute("versionList", service.getVersionList(originalId));
        model.addAttribute("originalId", originalId);
        return "documentMng/versionListStandalone"; // navTap 아님!
    }



    // 버전 상세 보기
    @GetMapping("/versionDetail")
    public String versionDetail(@RequestParam("versionId") Long versionId, Model model) {
        DocumentMngDTO dto = service.getVersionDetail(versionId);
        model.addAttribute("doc", dto);
        model.addAttribute("mainUrl", "documentMng/versionDetail");
        return "navTap";
    }
    
	/*
	 * // 작업중입니다 (필규) // 복원 처리
	 * 
	 * @PostMapping("/restoreVersion") public String
	 * restoreVersion(@RequestParam("versionId") Long versionId,
	 * 
	 * @RequestParam("originalId") Long originalId) {
	 * service.restoreVersion(versionId); return "redirect:/documentMng/main"; // 복원
	 * 후 이동할 화면 }
	 */
}
