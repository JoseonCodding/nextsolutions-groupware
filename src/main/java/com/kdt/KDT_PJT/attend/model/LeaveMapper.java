package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface LeaveMapper {

	//annual_leave (연차) 테이블에서 가져오기
	@Select("select * from annual_leave where employeeId = #{employeeId};")
	List<LeaveDTO> annualLeave(EmployeeDto dto); 
	
	//annual_leave 테이블에서 사용가능한 연차 가져오기
	@Select("select * from annual_leave where employeeId = #{employeeId} and leave_type='발생'  and (state_type is null or state_type = '반려')")
	List<LeaveDTO> annualLeaveRest(EmployeeDto dto); 
	
	
	// 단일 사용자의 총 연차 정보를 조회
	@Select("select total, ifnull(use_cnt,0) as used, t3.* from  "
			+ "(select count(*) as total from annual_leave  "
			+ "where employeeId =  #{employeeId} ) t1, "
			+ "(select count(*) as use_cnt from annual_leave "
			+ "where leave_type = '사용' and employeeId =  #{employeeId} ) t2, "
			+ "(select employeeId, emp_nm from employee where  employeeId =  #{employeeId} ) t3 "
			+ "; ")
	LeaveDTO getAnnualLeaveOne(EmployeeDto dto);
	
	// 관리자용 연차 조회 : 모든 사원의 연차 총합, 사용 연차, 이름을 한 번에 조회
	@Select("""
	    SELECT e.emp_nm AS empNm,
			   e.deptName AS deptName,
			   e.position AS position,
	           t1.employeeId,
	           t1.total,
	           IFNULL(t2.use_cnt, 0) AS used
	    FROM (
	        SELECT employeeId, COUNT(*) AS total
	        FROM annual_leave
	        GROUP BY employeeId
	    ) t1
	    LEFT JOIN (
	        SELECT employeeId, COUNT(*) AS use_cnt
	        FROM annual_leave
	        WHERE leave_type = '사용'
	        GROUP BY employeeId
	    ) t2 ON t1.employeeId = t2.employeeId
	    JOIN employee e ON t1.employeeId = e.employeeId
	    ORDER BY e.emp_nm
	""")
	List<LeaveDTO> mngLeaveList(); 
	

	// 관리자용 연차 조회 : 단일 사원의 연차 총합, 사용 연차, 이름을 한 번에 조회
	@Select("""
	    SELECT e.emp_nm AS empNm,
			   e.deptName AS deptName,
			   e.position AS position,
	           t1.employeeId,
	           t1.total,
	           IFNULL(t2.use_cnt, 0) AS used
	    FROM (
	        SELECT employeeId, COUNT(*) AS total
	        FROM annual_leave
	         WHERE employeeId = #{employeeId}   -- 단일 사원 필터 추가
	        GROUP BY employeeId
	    ) t1
	    LEFT JOIN (
	        SELECT employeeId, COUNT(*) AS use_cnt
	        FROM annual_leave
	        WHERE leave_type = '사용'
	        AND employeeId = #{employeeId}  -- 단일 사원 필터 추가
	        GROUP BY employeeId
	    ) t2 ON t1.employeeId = t2.employeeId
	    JOIN employee e ON t1.employeeId = e.employeeId
	    ORDER BY e.emp_nm
	""")
	LeaveDTO mngLeaveListOne(LeaveDTO one); 
	
	//관리자용 - 연차 테이블에서 가져오기
	@Select("select * from annual_leave where employeeId = #{employeeId};")
	List<LeaveDTO> annualLeaveOneMMM(LeaveDTO one);
	
	
	// 연차 사용 신청
	@Update("<script> "+

				"update annual_leave set approval_date = NOW(), used_reason = #{usedReason} ,"+
				" used_date=  #{usedDate} , state_type = '대기' where leave_id = #{leaveId} "+
			
			"</script> "
			)
	int approvalList(LeaveDTO dto); 


	 // 결재
	 @Insert("""
			 INSERT INTO schedule (
			   title, start_date, end_date, cate, content,
			   created_at, updated_at, employeeId, repeat_check, holiday
			 )
			 SELECT
			   CONCAT(e.emp_nm, ' 연차')     AS title,          -- 사원명 + " 연차"
			   al.used_date                 AS start_date,
			   al.used_date                 AS end_date,
			   '종일'                        AS cate,
			   al.used_reason               AS content,
			   NOW()                        AS created_at,
			   NOW()                        AS updated_at,
			   al.employeeId                AS employeeId,     -- 연차 주인의 캘린더 생성
			   0                            AS repeat_check,   -- 숫자형이면 0, 문자형이면 '0'
			   '연차'                        AS holiday
			 FROM annual_leave al
			 JOIN employee e
			   ON al.employeeId = e.employeeId
			 WHERE al.leave_id = #{pkId}
			   AND al.used_date IS NOT NULL
			 """)
	 int insertScheduleRest(@Param("pkId") String pkId);


}
