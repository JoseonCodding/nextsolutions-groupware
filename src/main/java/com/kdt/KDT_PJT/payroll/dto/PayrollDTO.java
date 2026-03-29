package com.kdt.KDT_PJT.payroll.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PayrollDTO {

    private Integer payrollId;
    private String  employeeId;
    private String  empNm;
    private String  deptName;
    private String  position;
    private Integer companyId;
    private String  yearMonth;      // 'yyyy-MM'

    // ── 지급 ──────────────────────────────────────────────────
    private long baseSalary;
    private long mealAllowance;
    private long transportAllowance;
    private long overtimePay;
    private long totalPay;

    // ── 공제 ──────────────────────────────────────────────────
    private long deductPension;    // 국민연금 4.5%
    private long deductHealth;     // 건강보험 3.545%
    private long deductCare;       // 장기요양 (건강보험료×12.95%)
    private long deductEmploy;     // 고용보험 0.9%
    private long deductTax;        // 소득세
    private long deductLocalTax;   // 지방소득세 (소득세×10%)
    private long totalDeduct;

    // ── 실수령 ────────────────────────────────────────────────
    private long netPay;

    // ── 근태 ──────────────────────────────────────────────────
    private int workDays;
    private int absenceDays;

    // ── 상태 ──────────────────────────────────────────────────
    private String status;          // 대기 / 확정 / 지급완료
    private String note;
}
