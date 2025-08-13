package com.kdt.KDT_PJT.schedule.model;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface ScheduleMapper {

	//일정 등록
	@Insert("insert into schedule "+
			"(title,    content,      cate,   start_date,   start_time,   end_date,   end_time,  repeat_check,  holiday, alarm,  created_at, employeeId) values "+
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
	
	//일정 상세보기
	@Select("SELECT * FROM schedule WHERE schedule_id = #{scheduleId}")
	ScheduleDTO getScheduleDetail(ScheduleDTO dto);

	//일정 수정
	@Update("""
		    UPDATE schedule
		    SET
		        title         = #{title},
		        content       = #{content},
		        cate          = #{cate},
		        start_date    = #{startDate},
		        start_time    = #{startTime},
		        end_date      = #{endDate},
		        end_time      = #{endTime},
		        repeat_check  = #{repeatCheck},
		        holiday       = #{holiday},
		        alarm         = #{alarm},
		        updated_at    = NOW()
		    WHERE schedule_id = #{scheduleId}
		      AND employeeId = #{employeeId}
		""")

	int modify(ScheduleDTO dto);

	//일정 삭제
	@Delete("""
		    DELETE FROM schedule
		    WHERE schedule_id = #{scheduleId}
		""")
	int delete(ScheduleDTO dto);

}
