package com.kdt.KDT_PJT.schedule.model;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface ScheduleMapper {

	//일정 등록
	@Insert("insert into schedule "+
			"(title, content, cate, start_date, end_date, created_at, employeeId) values "+
			"(#{title}, #{content}, #{cate}, #{startDate}, #{endDate} ,now(), #{employeeId} ) "
			)
	int insert(ScheduleDTO dto); 
	
	@Select("""
		    SELECT * 
		    FROM schedule
		    WHERE start_date <= #{endDate} 
		      AND end_date >= #{startDate} 
		    ORDER BY start_date 
		""")
		List<ScheduleDTO> getScheduleListByMonth(
		    @Param("startDate") Date startDate,
		    @Param("endDate") Date endDate
		);
}
