package com.kdt.KDT_PJT.payroll.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kdt.KDT_PJT.payroll.dto.PayrollDTO;
import com.kdt.KDT_PJT.payroll.dto.SalarySettingDTO;

@Mapper
public interface PayrollMapper {

    // ── 기본급 설정 ────────────────────────────────────────────

    /** 회사 전체 직원 + 최신 기본급 조회 (관리자용) */
    List<SalarySettingDTO> selectSalaryList(@Param("companyId") int companyId);

    /** 특정 직원의 최신 기본급 조회 */
    SalarySettingDTO selectLatestSalary(@Param("employeeId") String employeeId);

    /** 기본급 저장 (upsert) */
    void upsertSalary(SalarySettingDTO dto);

    // ── 근태 집계 ──────────────────────────────────────────────

    /** 특정 월 실 근무일수 */
    int countWorkDays(@Param("employeeId") String employeeId,
                      @Param("yearMonth") String yearMonth);

    /** 해당 월 영업일수 (공휴일 제외 평일) */
    int countBizDays(@Param("yearMonth") String yearMonth,
                     @Param("companyId") int companyId);

    // ── 급여명세서 ─────────────────────────────────────────────

    /** 급여명세서 저장 (upsert) */
    void upsertPayroll(PayrollDTO dto);

    /** 월별 전체 급여 목록 (관리자) */
    List<PayrollDTO> selectPayrollList(@Param("yearMonth") String yearMonth,
                                       @Param("companyId") int companyId);

    /** 직원 본인 급여 이력 */
    List<PayrollDTO> selectMyPayrollList(@Param("employeeId") String employeeId,
                                          @Param("companyId") int companyId);

    /** 급여명세서 단건 조회 */
    PayrollDTO selectPayroll(@Param("employeeId") String employeeId,
                              @Param("yearMonth") String yearMonth);

    /** 급여 상태 변경 */
    void updatePayrollStatus(@Param("payrollId") int payrollId,
                              @Param("status") String status);
}
