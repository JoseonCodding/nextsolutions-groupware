package com.kdt.KDT_PJT.payroll.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.payroll.dto.PayrollDTO;
import com.kdt.KDT_PJT.payroll.dto.SalarySettingDTO;
import com.kdt.KDT_PJT.payroll.mapper.PayrollMapper;
import com.kdt.KDT_PJT.payroll.service.PayrollService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired private PayrollMapper  payrollMapper;
    @Autowired private PayrollService payrollService;

    private static final String THIS_MONTH =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

    // ── 관리자: 급여 관리 메인 ─────────────────────────────────

    @GetMapping("/main")
    public String main(@RequestParam(defaultValue = "") String yearMonth,
                       HttpSession session, Model model) {
        EmployeeDto me = me(session);
        if (me == null) return "redirect:/login";
        if (!"대표".equals(me.getRole())) return "redirect:/dashboard";

        if (yearMonth.isBlank()) yearMonth = THIS_MONTH;

        List<PayrollDTO>        payrollList = payrollMapper.selectPayrollList(yearMonth, me.getCompanyId());
        List<SalarySettingDTO>  salaryList  = payrollMapper.selectSalaryList(me.getCompanyId());

        model.addAttribute("payrollList", payrollList);
        model.addAttribute("salaryList",  salaryList);
        model.addAttribute("yearMonth",   yearMonth);
        model.addAttribute("mainUrl", "payroll/payroll_main");
        return "navTap";
    }

    // ── 관리자: 급여 일괄 계산 ─────────────────────────────────

    @PostMapping("/calculate")
    @ResponseBody
    public Map<String, Object> calculate(@RequestParam String yearMonth,
                                          HttpSession session) {
        EmployeeDto me = me(session);
        if (me == null || !"대표".equals(me.getRole()))
            return Map.of("ok", false, "msg", "권한 없음");
        try {
            int cnt = payrollService.calculateAll(yearMonth, me.getCompanyId());
            return Map.of("ok", true, "count", cnt);
        } catch (Exception e) {
            return Map.of("ok", false, "msg", e.getMessage());
        }
    }

    // ── 관리자: 기본급 저장 ────────────────────────────────────

    @PostMapping("/salary/save")
    @ResponseBody
    public Map<String, Object> saveSalary(SalarySettingDTO dto, HttpSession session) {
        EmployeeDto me = me(session);
        if (me == null || !"대표".equals(me.getRole()))
            return Map.of("ok", false, "msg", "권한 없음");

        dto.setCompanyId(me.getCompanyId());
        if (dto.getEffectiveFrom() == null || dto.getEffectiveFrom().isBlank())
            dto.setEffectiveFrom(LocalDate.now().withDayOfMonth(1).toString());

        try {
            payrollMapper.upsertSalary(dto);
            return Map.of("ok", true);
        } catch (Exception e) {
            return Map.of("ok", false, "msg", e.getMessage());
        }
    }

    // ── 관리자: 급여 상태 변경 ─────────────────────────────────

    @PostMapping("/status")
    @ResponseBody
    public Map<String, Object> updateStatus(@RequestParam int payrollId,
                                             @RequestParam String status,
                                             HttpSession session) {
        EmployeeDto me = me(session);
        if (me == null || !"대표".equals(me.getRole()))
            return Map.of("ok", false, "msg", "권한 없음");
        if (!List.of("대기", "확정", "지급완료").contains(status))
            return Map.of("ok", false, "msg", "잘못된 상태");
        payrollMapper.updatePayrollStatus(payrollId, status);
        return Map.of("ok", true);
    }

    // ── 직원: 내 급여명세서 ────────────────────────────────────

    @GetMapping("/my")
    public String myPayroll(HttpSession session, Model model) {
        EmployeeDto me = me(session);
        if (me == null) return "redirect:/login";

        List<PayrollDTO> list = payrollMapper.selectMyPayrollList(
                me.getEmployeeId(), me.getCompanyId());
        model.addAttribute("payrollList", list);
        model.addAttribute("mainUrl", "payroll/payroll_slip");
        return "navTap";
    }

    // ── 공통 ───────────────────────────────────────────────────

    private EmployeeDto me(HttpSession session) {
        return (EmployeeDto) session.getAttribute("loginUser");
    }
}
