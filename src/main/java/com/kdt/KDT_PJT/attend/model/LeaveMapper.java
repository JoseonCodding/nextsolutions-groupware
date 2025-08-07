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
	
	//user_id 기준으로 특정 사용자의 연차 정보(leave_type, leave_hours)를 조회
	@Select("SELECT leave_type, leave_hours " +
            "FROM annual_leave " +
            "WHERE user_id = #{userId}")
    List<LeaveDTO> getAnnualLeaveByUserId(@Param("userId") Long userId);
}
