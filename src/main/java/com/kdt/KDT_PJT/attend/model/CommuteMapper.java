package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommuteMapper {

	 // 전월 정상근무 일수
	@Select("""
		    SELECT COUNT(DISTINCT DATE(check_in_time))
		    FROM attendance
		    WHERE employeeId = #{employeeId}
		      AND DATE(check_in_time) BETWEEN DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01')
		                                AND LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
		      AND is_normal_work = 1
		""")
    int getLastMonthNormalWorkDays(String employeeId);

	// 해당 월 정상근무 목록
	@Select("""
		    SELECT employeeId, count(*) as work_cnt FROM attendance where
		Date(check_in_time) >= #{startDay} and Date(check_in_time) <= #{endDay}
        and TIME(check_in_time) <= '09:00:00'  and TIME(check_out_time) >= '18:00:00'
		group by employeeId; 
	""")
    List<AttendDTO> getLastMonthTotalWorkDays(AttendDTO dto);

    // 전체 직원 목록 조회
    @Select("SELECT employeeId FROM employee")
    List<String> getAllEmployeeIds();
}

