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
	 int insertAutoLeave(AttendDTO dto);
	 
	// 전월 근무일수
//	@Select("""
//		    SELECT COUNT(DISTINCT DATE(check_in_time))
//		    FROM attendance
//		    WHERE employeeId = #{employeeId}
//		      AND DATE(check_in_time) BETWEEN DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01')
//		                                AND LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
//		""")
//    int getLastMonthNormalWorkDays(String employeeId);
	
	// 회사의 평일 휴무일 개수
    @Select("SELECT COUNT(*) AS holiday_count "
    		+ "FROM ( "
    		+ "    SELECT DATE_ADD("
    		+ "               GREATEST(start_date, #{startDay}), "
    		+ "               INTERVAL seq DAY "
    		+ "           ) AS the_date "
    		+ "    FROM schedule "
    		+ "    JOIN ( "
    		+ "        SELECT 0 AS seq UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 "
    		+ "        UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 "
    		+ "        UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 "
    		+ "        UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 "
    		+ "        UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 "
    		+ "        UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 "
    		+ "        UNION ALL SELECT 30 UNION ALL SELECT 31 "
    		+ "    ) AS seq_table "
    		+ "    WHERE seq <= DATEDIFF(LEAST(end_date, #{endDay}), GREATEST(start_date, #{startDay})) "
    		+ "      AND holiday = '휴무일' "
    		+ ") AS expanded_dates "
    		+ "WHERE DAYOFWEEK(the_date) NOT IN (1,7) ")
    int getHolidays(AttendDTO dto);

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

