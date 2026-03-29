package com.kdt.KDT_PJT.payroll.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.payroll.dto.PayrollDTO;
import com.kdt.KDT_PJT.payroll.dto.SalarySettingDTO;
import com.kdt.KDT_PJT.payroll.mapper.PayrollMapper;

@Service
public class PayrollService {

    // 4대보험 공제율 (2024 기준)
    private static final double PENSION_RATE = 0.045;   // 국민연금
    private static final double HEALTH_RATE  = 0.03545; // 건강보험
    private static final double CARE_RATE    = 0.1295;  // 장기요양 (건강보험료×12.95%)
    private static final double EMPLOY_RATE  = 0.009;   // 고용보험

    @Autowired
    private PayrollMapper payrollMapper;

    /** 단일 직원 급여 계산 후 저장 */
    public PayrollDTO calculate(String employeeId, String yearMonth, int companyId) {
        SalarySettingDTO salary = payrollMapper.selectLatestSalary(employeeId);

        long baseSalary         = salary != null ? salary.getBaseSalary()         : 0L;
        long mealAllowance      = salary != null ? salary.getMealAllowance()      : 100_000L;
        long transportAllowance = salary != null ? salary.getTransportAllowance() : 50_000L;

        // 근태: 실 근무일수 / 영업일수
        int workDays = payrollMapper.countWorkDays(employeeId, yearMonth);
        int bizDays  = payrollMapper.countBizDays(yearMonth, companyId);
        if (bizDays == 0) bizDays = 22; // 방어값

        // 일할 계산 (결근 있을 경우 기본급 비례 감액)
        long actualBase = bizDays > 0
                ? Math.round(baseSalary * (double) workDays / bizDays)
                : baseSalary;
        int absenceDays = Math.max(0, bizDays - workDays);

        // 총 지급액 (수당은 일할 미적용)
        long totalPay = actualBase + mealAllowance + transportAllowance;

        // 공제: 기본급 + 수당 합산 기준 (식대/교통비는 비과세지만 단순화)
        long taxBase = actualBase; // 공제 기준은 기본급

        long deductPension  = round(taxBase * PENSION_RATE);
        long deductHealth   = round(taxBase * HEALTH_RATE);
        long deductCare     = round(deductHealth * CARE_RATE);
        long deductEmploy   = round(taxBase * EMPLOY_RATE);
        long deductTax      = calcIncomeTax(taxBase);
        long deductLocalTax = round(deductTax * 0.1);
        long totalDeduct    = deductPension + deductHealth + deductCare
                              + deductEmploy + deductTax + deductLocalTax;

        long netPay = Math.max(0, totalPay - totalDeduct);

        PayrollDTO dto = new PayrollDTO();
        dto.setEmployeeId(employeeId);
        dto.setCompanyId(companyId);
        dto.setYearMonth(yearMonth);
        dto.setBaseSalary(actualBase);
        dto.setMealAllowance(mealAllowance);
        dto.setTransportAllowance(transportAllowance);
        dto.setOvertimePay(0L);
        dto.setTotalPay(totalPay);
        dto.setDeductPension(deductPension);
        dto.setDeductHealth(deductHealth);
        dto.setDeductCare(deductCare);
        dto.setDeductEmploy(deductEmploy);
        dto.setDeductTax(deductTax);
        dto.setDeductLocalTax(deductLocalTax);
        dto.setTotalDeduct(totalDeduct);
        dto.setNetPay(netPay);
        dto.setWorkDays(workDays);
        dto.setAbsenceDays(absenceDays);
        dto.setStatus("대기");

        payrollMapper.upsertPayroll(dto);
        return dto;
    }

    /** 회사 전체 직원 일괄 계산 */
    public int calculateAll(String yearMonth, int companyId) {
        List<SalarySettingDTO> employees = payrollMapper.selectSalaryList(companyId);
        for (SalarySettingDTO emp : employees) {
            try {
                calculate(emp.getEmployeeId(), yearMonth, companyId);
            } catch (Exception ignored) {}
        }
        return employees.size();
    }

    // ── helpers ──────────────────────────────────────────────────

    /** 소득세 간이세액 (월급여 기준 단순화) */
    private long calcIncomeTax(long monthly) {
        if (monthly <= 1_000_000)  return 0L;
        if (monthly <= 2_000_000)  return round(monthly * 0.006);
        if (monthly <= 4_000_000)  return round(monthly * 0.015);
        if (monthly <= 6_000_000)  return round(monthly * 0.024);
        if (monthly <= 8_000_000)  return round(monthly * 0.035);
        return round(monthly * 0.038);
    }

    private long round(double v) {
        // 10원 단위 반올림
        return Math.round(v / 10.0) * 10L;
    }
}
