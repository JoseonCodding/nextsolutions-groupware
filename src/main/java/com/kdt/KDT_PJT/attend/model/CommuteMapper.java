package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommuteMapper {

	 // 전월 정상근무 일수
    @Select("""
        SELECT COUNT(*) 
        FROM commute 
        WHERE employeeId = #{employeeId}
          AND work_date BETWEEN DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m-01')
                            AND LAST_DAY(DATE_SUB(NOW(), INTERVAL 1 MONTH))
          AND status = '정상'
    """)
    int getLastMonthNormalWorkDays(String employeeId);

    // 전월 전체 근무일수 (평일만)
    @Select("""
        SELECT COUNT(*) 
        FROM commute 
        WHERE employeeId = #{employeeId}
          AND work_date BETWEEN DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m-01')
                            AND LAST_DAY(DATE_SUB(NOW(), INTERVAL 1 MONTH))
    """)
    int getLastMonthTotalWorkDays(String employeeId);

    // 전체 직원 목록 조회
    @Select("SELECT employeeId FROM employee")
    List<String> getAllEmployeeIds();
}

