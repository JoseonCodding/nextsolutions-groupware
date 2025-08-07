package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LeaveMapper {

	//annual_leave 테이블에서 가져오기
	@Select("select * from annual_leave where employeeId = '20250001';")
	List<LeaveDTO> annualLeave(); 
	
	//employee 테이블에서 가져오기
	@Select("SELECT * FROM employee  where employeeId = '20250001' ")
	LeaveDTO name(); 
	
	//emp_seq 기준으로 특정 사용자의 총 연차 정보를 조회
	@Select("select count(*) as yc from annual_leave "
			+ "where employeeId = #{employeeId} and leave_type = #{leave_type} ")
	int getAnnualLeaveCnt(LeaveDTO dto);
	
	//관리자용 연차 조회
	@Select("select t1.*, ifnull(use_cnt,0) as used from "
			+ "(select employeeId, count(*) as total from annual_leave "
			+ "where leave_type = '발생' "
			+ "group by employeeId ) t1 "
			+ "left outer join "
			+ "(select employeeId, count(*) use_cnt from annual_leave "
			+ "where leave_type = '사용' "
			+ "group by employeeId) t2 "
			+ "on t1.employeeId = t2.employeeId; ")
	LeaveDTO mngLeaveList(); 
	
	//user_id 기준으로 특정 사용자의 연차 정보(leave_type, leave_hours)를 조회
	@Select("SELECT leave_type, leave_hours " +
            "FROM annual_leave " +
            "WHERE user_id = #{userId}")
    List<LeaveDTO> getAnnualLeaveByUserId(@Param("userId") Long userId);
}
