package com.kdt.KDT_PJT.schedule.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface ScheduleMapper {

	//일정 등록
	@Insert("insert into schedule "+
			"(title,    content,      cate,   start_date,   start_time,   end_date,   end_time, repeat_check,  alarm, holiday, created_at, employeeId) values "+
			"(#{title}, #{content}, #{cate}, #{startDate}, #{startTime}, #{endDate}, #{endTime}, #{repeatCheck} , #{holiday} , #{alarm} ,now(), #{employeeId} ) "
			)
	int insert(ScheduleDTO dto); 
	
	@Select("""
		    SELECT * 
		    FROM schedule
		    WHERE start_date <= #{endDate} 
		      AND end_date >= #{startDate} 
		    ORDER BY start_date 
		""")
		List<ScheduleDTO> getScheduleListByMonth(ScheduleDTO dto);
}
