package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface AttendMapper {

	//출근 시간 기록
	@Insert("INSERT INTO attendance (employeeId, check_in_time) VALUES (#{employeeId}, #{checkInTime})")
    void insertAttendance(AttendDTO attendance);

	//퇴근 시간 기록
	@Update("UPDATE attendance SET check_out_time = #{checkOutTime} WHERE employeeId = #{employeeId} AND DATE(check_in_time) = CURDATE()")
	void updateAttendance(AttendDTO attendance);
	
	//
	@Select("select * from attendance")
	List<AttendDTO> sellectAttendList();
	
	//
	@Select("select * from employee where employeeId = #{employeeId} and emp_nm = #{emp_nm}")
	String empName(EmployeeDto empDto);
}
