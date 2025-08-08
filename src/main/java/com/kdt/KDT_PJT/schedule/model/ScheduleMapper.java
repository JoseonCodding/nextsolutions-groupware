package com.kdt.KDT_PJT.schedule.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ScheduleMapper {

	@Insert("insert into schedule "+
			"(title, content, create_at) values "+
			"(#{title}, #{content},  now() )"
			)
	int insert(ScheduleDTO dto); 
}
