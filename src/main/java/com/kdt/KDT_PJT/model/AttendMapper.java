package com.kdt.KDT_PJT.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AttendMapper {

	@Insert("INSERT INTO attendance (user_id, check_in_time) VALUES (#{userId}, #{checkInTime})")
    void insertAttendance(AttendDTO attendance);
	
	@Update("UPDATE attendance SET check_out_time = #{checkOutTime} WHERE user_id = #{userId} AND DATE(check_in_time) = CURDATE()")
	void updateAttendance(AttendDTO attendance);
	
	@Select("select * from attendance")
	List<AttendDTO> sellectAttendList();
	
}
