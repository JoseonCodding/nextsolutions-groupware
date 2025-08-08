package com.kdt.KDT_PJT.schedule.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ScheduleMapper {

	@Insert("insert into schedule "+
			"(title, content, cate, start_date, end_date, created_at) values "+
			"(#{title}, #{content}, #{cate}, #{startDate}, #{endDate} ,now() ) "
			)
	int insert(ScheduleDTO dto); 
	
}
