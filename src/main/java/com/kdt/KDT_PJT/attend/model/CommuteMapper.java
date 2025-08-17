package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommuteMapper {

	//연차 자동 부여
	 @Insert("""
		        INSERT INTO annual_leave 
		        (employeeId, create_date, leave_type, create_reason) VALUES 
		        (#{employeeId}, now(), '발생', '전월 근무율 80% 이상 자동 부여')
		    """)
	 int insertAutoLeave();
	 
	// 전월 근무일수
//	@Select("""
//		    SELECT COUNT(DISTINCT DATE(check_in_time))
//		    FROM attendance
//		    WHERE employeeId = #{employeeId}
//		      AND DATE(check_in_time) BETWEEN DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01')
//		                                AND LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
//		""")
//    int getLastMonthNormalWorkDays(String employeeId);
	
	// 주말+회사 휴무일 개수 (total_off_days)
    @Select("WITH RECURSIVE month_dates AS ( "
    		+ "    SELECT DATE(CONCAT(DATE(#{startDay}))) AS the_date "
    		+ "    UNION ALL "
    		+ "    SELECT DATE_ADD(the_date, INTERVAL 1 DAY) "
    		+ "    FROM month_dates "
    		+ "    WHERE the_date < LAST_DAY(CONCAT(DATE(#{startDay}))) "
    		+ ") "
    		+ "SELECT "
    		+ "    COUNT(CASE WHEN DAYOFWEEK(the_date) IN (1,7) THEN 1 END) AS weekend_count, "
    		+ "    COUNT(CASE WHEN s.start_date IS NOT NULL THEN 1 END) AS holiday_count, "
    		+ "    COUNT(CASE WHEN DAYOFWEEK(the_date) IN (1,7) OR s.start_date IS NOT NULL THEN 1 END) AS total_off_days "
    		+ "FROM month_dates md "
    		+ "LEFT JOIN schedule s "
    		+ "    ON md.the_date BETWEEN s.start_date AND s.end_date "
    		+ "    AND s.holiday = '휴무일' ")
    int getHolidays();

	// 해당 월 정상근무 목록
	@Select("""
		    SELECT employeeId, count(*) as work_cnt FROM attendance where
			Date(check_in_time) >= #{startDay} and Date(check_in_time) <= #{endDay}
	        and TIME(check_in_time) <= '09:00:00'  and TIME(check_out_time) >= '18:00:00'
			group by employeeId
	""")
    List<AttendDTO> getLastMonthTotalWorkDays(AttendDTO dto);

    // 전체 직원 목록 조회
    @Select("SELECT employeeId FROM employee")
    List<String> getAllEmployeeIds();
}

