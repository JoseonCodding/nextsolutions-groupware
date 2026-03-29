package com.kdt.KDT_PJT.company.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdt.KDT_PJT.company.dto.CompanyRegisterDto;
import com.kdt.KDT_PJT.company.mapper.CompanyMapper;
import com.kdt.KDT_PJT.company.service.CompanyService;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapper companyMapper;

    // ── 회사 관리 (대표만 접근) ──────────────────────────────────

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null || !"대표".equals(me.getRole())) return "redirect:/dashboard";
        java.util.List<java.util.Map<String, Object>> companies = new java.util.ArrayList<>();
        long totalEmp = 0;
        long proCnt = 0;
        try {
            companies = companyMapper.selectAllCompanies();
            totalEmp = companies.stream()
                .mapToLong(c -> c.get("emp_count") != null ? ((Number) c.get("emp_count")).longValue() : 0)
                .sum();
            proCnt = companies.stream()
                .filter(c -> "PRO".equals(c.get("plan")))
                .count();
        } catch (Exception e) {
            // DB 오류 시 빈 목록으로 표시
        }
        model.addAttribute("companies", companies);
        model.addAttribute("totalEmp", totalEmp);
        model.addAttribute("proCnt", proCnt);
        model.addAttribute("mainUrl", "company/company_admin");
        return "navTap";
    }

    @GetMapping("/admin/stats")
    @ResponseBody
    public Map<String, Object> companyStats(@RequestParam("companyId") int companyId,
                                            HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null || !"대표".equals(me.getRole())) return Map.of("error", "권한 없음");
        return companyMapper.selectCompanyStats(companyId);
    }

    @PostMapping("/admin/updatePlan")
    @ResponseBody
    public Map<String, Object> updatePlan(@RequestParam("companyId") int companyId,
                                          @RequestParam("plan") String plan,
                                          HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null || !"대표".equals(me.getRole())) return Map.of("success", false);
        if (!java.util.List.of("FREE","BASIC","PRO").contains(plan)) return Map.of("success", false);
        int rows = companyMapper.updatePlan(companyId, plan);
        return Map.of("success", rows > 0);
    }

    @PostMapping("/admin/updateNm")
    @ResponseBody
    public Map<String, Object> updateNm(@RequestParam("companyId") int companyId,
                                        @RequestParam("companyNm") String companyNm,
                                        HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null || !"대표".equals(me.getRole())) return Map.of("success", false);
        int rows = companyMapper.updateCompanyNm(companyId, companyNm.trim());
        return Map.of("success", rows > 0);
    }

    // 회사 가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("dto", new CompanyRegisterDto());
        return "company/companyRegister";
    }

    // 회사 가입 처리
    @PostMapping("/register")
    public String registerProcess(@ModelAttribute CompanyRegisterDto dto, Model model, HttpSession session) {

        // 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            model.addAttribute("dto", dto);
            model.addAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
            return "company/companyRegister";
        }

        // 이메일 중복 확인
        if (companyService.isEmailDuplicate(dto.getOwnerEmail())) {
            model.addAttribute("dto", dto);
            model.addAttribute("errorMsg", "이미 등록된 이메일입니다.");
            return "company/companyRegister";
        }

        // 등록 처리
        String adminId = companyService.register(dto);

        // 첫 로그인 후 온보딩 wizard 표시 플래그
        session.setAttribute("showOnboarding", true);

        return "redirect:/login?registered=true&adminId=" + adminId;
    }
}
