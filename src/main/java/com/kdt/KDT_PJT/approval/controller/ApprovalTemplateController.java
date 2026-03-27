package com.kdt.KDT_PJT.approval.controller;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.approval.mapper.ApprovalTemplateMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDocDTO;
import com.kdt.KDT_PJT.approval.model.ApprovalTemplateDTO;
import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.notification.NotificationMapper;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/approval")
public class ApprovalTemplateController {

    @Autowired private ApprovalTemplateMapper templateMapper;
    @Autowired private PjtMngMapper pjtMngMapper;
    @Autowired private NotificationMapper notificationMapper;

    @ModelAttribute("navUrl")
    public String navUrl() { return "approval/approvalNav"; }

    // ─────────────────────────────────────────────
    // 템플릿 관리 (대표만)
    // ─────────────────────────────────────────────

    @GetMapping("/template")
    public String templateList(HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        boolean isAdmin = "대표".equals(me.getRole());
        List<ApprovalTemplateDTO> list = templateMapper.selectTemplateList(CompanyContext.get());
        model.addAttribute("templateList", list);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("mainUrl", "approval/template_list");
        return "navTap";
    }

    @GetMapping("/template/form")
    public String templateForm(@RequestParam(required = false) Integer templateId,
                                HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (!"대표".equals(me.getRole())) return "redirect:/approval/template";

        ApprovalTemplateDTO dto = new ApprovalTemplateDTO();
        if (templateId != null) {
            dto = templateMapper.selectTemplateById(templateId);
        }
        model.addAttribute("template", dto);
        model.addAttribute("mainUrl", "approval/template_form");
        return "navTap";
    }

    @PostMapping("/template/save")
    public String templateSave(ApprovalTemplateDTO dto, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (!"대표".equals(me.getRole())) return "redirect:/approval/template";

        dto.setCompanyId(CompanyContext.get());
        dto.setCreatedBy(me.getEmployeeId());
        dto.setContent(Jsoup.clean(dto.getContent() != null ? dto.getContent() : "", Safelist.none()));

        if (dto.getTemplateId() == null) {
            templateMapper.insertTemplate(dto);
        } else {
            templateMapper.updateTemplate(dto);
        }
        return "redirect:/approval/template";
    }

    // 템플릿 내용 조회 API (결재 신청 폼에서 AJAX로 호출)
    @GetMapping("/template/content")
    @org.springframework.web.bind.annotation.ResponseBody
    public ApprovalTemplateDTO templateContent(@RequestParam Integer templateId) {
        return templateMapper.selectTemplateById(templateId);
    }

    @PostMapping("/template/delete")
    public String templateDelete(@RequestParam Integer templateId, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (!"대표".equals(me.getRole())) return "redirect:/approval/template";
        templateMapper.deleteTemplate(templateId, CompanyContext.get());
        return "redirect:/approval/template";
    }

    // ─────────────────────────────────────────────
    // 자유양식 결재 신청
    // ─────────────────────────────────────────────

    @GetMapping("/doc/new")
    public String docNewForm(@RequestParam(required = false) Integer templateId,
                              HttpSession session, Model model) {
        Integer companyId = CompanyContext.get();
        List<ApprovalTemplateDTO> templates = templateMapper.selectTemplateList(companyId);
        List<CmmnMap> approvers = pjtMngMapper.getEmployeeList(companyId);

        ApprovalTemplateDTO selected = null;
        if (templateId != null) {
            selected = templateMapper.selectTemplateById(templateId);
        }
        model.addAttribute("templates", templates);
        model.addAttribute("approvers", approvers);
        model.addAttribute("selected", selected);
        model.addAttribute("mainUrl", "approval/doc_form");
        return "navTap";
    }

    @PostMapping("/doc/submit")
    public String docSubmit(ApprovalDocDTO dto, HttpSession session,
                             RedirectAttributes ra) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        dto.setWriterId(me.getEmployeeId());
        dto.setWriterName(me.getEmpNm());
        dto.setCompanyId(CompanyContext.get());
        dto.setContent(Jsoup.clean(dto.getContent() != null ? dto.getContent() : "", Safelist.none()));

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            ra.addFlashAttribute("errorMsg", "제목을 입력하세요.");
            return "redirect:/approval/doc/new";
        }

        templateMapper.insertDoc(dto);

        // 결재자에게 알림
        if (dto.getApproverId() != null && !dto.getApproverId().isBlank()) {
            notificationMapper.insert(CompanyContext.get(), dto.getApproverId(),
                    me.getEmpNm(), "결재요청",
                    me.getEmpNm() + "님이 결재를 요청했습니다: " + dto.getTitle(),
                    "/approval/doc/view?docId=" + dto.getDocId());
        }
        return "redirect:/approval/doc/list";
    }

    // ─────────────────────────────────────────────
    // 자유양식 결재 목록
    // ─────────────────────────────────────────────

    @GetMapping("/doc/list")
    public String docList(HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        boolean isAdmin = "대표".equals(me.getRole());
        List<ApprovalDocDTO> list = templateMapper.selectDocList(
                CompanyContext.get(), me.getEmployeeId(), isAdmin);
        model.addAttribute("docList", list);
        model.addAttribute("mainUrl", "approval/doc_list");
        return "navTap";
    }

    // ─────────────────────────────────────────────
    // 자유양식 결재 상세 / 승인 / 반려
    // ─────────────────────────────────────────────

    @GetMapping("/doc/view")
    public String docView(@RequestParam Integer docId, HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        ApprovalDocDTO doc = templateMapper.selectDocById(docId, CompanyContext.get());
        if (doc == null) return "redirect:/approval/doc/list";

        boolean canApprove = doc.getApproverId() != null
                && doc.getApproverId().equals(me.getEmployeeId())
                && "대기".equals(doc.getStatus());
        boolean canDelete = doc.getWriterId().equals(me.getEmployeeId())
                && "대기".equals(doc.getStatus());

        model.addAttribute("doc", doc);
        model.addAttribute("canApprove", canApprove);
        model.addAttribute("canDelete", canDelete);
        model.addAttribute("loginUser", me);
        model.addAttribute("mainUrl", "approval/doc_viewer");
        return "navTap";
    }

    @PostMapping("/doc/approve")
    public String docApprove(@RequestParam Integer docId, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        ApprovalDocDTO doc = templateMapper.selectDocById(docId, CompanyContext.get());
        if (doc == null || !me.getEmployeeId().equals(doc.getApproverId())) {
            return "redirect:/approval/doc/list";
        }
        templateMapper.updateDocStatus(docId, "완료", me.getEmployeeId(), me.getEmpNm(), null);

        notificationMapper.insert(CompanyContext.get(), doc.getWriterId(),
                me.getEmpNm(), "결재승인",
                "결재가 승인되었습니다: " + doc.getTitle(),
                "/approval/doc/view?docId=" + docId);
        return "redirect:/approval/doc/view?docId=" + docId;
    }

    @PostMapping("/doc/reject")
    public String docReject(@RequestParam Integer docId,
                             @RequestParam String rejectReason,
                             HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        ApprovalDocDTO doc = templateMapper.selectDocById(docId, CompanyContext.get());
        if (doc == null || !me.getEmployeeId().equals(doc.getApproverId())) {
            return "redirect:/approval/doc/list";
        }
        templateMapper.updateDocStatus(docId, "반려", me.getEmployeeId(), me.getEmpNm(), rejectReason);

        notificationMapper.insert(CompanyContext.get(), doc.getWriterId(),
                me.getEmpNm(), "결재반려",
                "결재가 반려되었습니다: " + doc.getTitle(),
                "/approval/doc/view?docId=" + docId);
        return "redirect:/approval/doc/view?docId=" + docId;
    }

    @PostMapping("/doc/delete")
    public String docDelete(@RequestParam Integer docId, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        templateMapper.softDeleteDoc(docId, me.getEmployeeId());
        return "redirect:/approval/doc/list";
    }
}
