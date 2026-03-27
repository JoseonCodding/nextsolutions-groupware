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
	@Insert("""
		    INSERT INTO schedule
		    (title, content, cate, start_date, start_time, end_date, end_time, repeat_check, holiday, alarm, created_at, employeeId, company_id)
		    VALUES
		    (#{title}, #{content}, #{cate}, #{startDate}, #{startTime}, #{endDate}, #{endTime}, #{repeatCheck},
		     CASE
		        WHEN #{holiday} IS NULL OR #{holiday} = '' THEN '일정'
		        ELSE #{holiday}
		     END,
		     #{alarm}, NOW(), #{employeeId}, #{companyId})
		""")
	int insert(ScheduleDTO dto);

	@Select("""
		     SELECT *
		    FROM schedule
		    WHERE company_id = #{companyId}
		    ORDER BY start_date
		""")
	List<ScheduleDTO> getScheduleListByMonth(ScheduleDTO dto);


	@Select("""
		     SELECT *
		    FROM schedule
		    WHERE repeat_check < 2
		      AND company_id = #{companyId}
		    ORDER BY start_date
		""")
	List<ScheduleDTO> getScheduleListRepeatEmpty(ScheduleDTO dto);


	@Select("""

		    select t1.PJT_SN, t1.PJT_BGNG_DT as start_date, t1.PJT_END_DT as end_date, '종일' as cate, t1.PJT_NM as title, t1.docType as holiday, t1.PJT_STTS_CD
			from TB_PJT_BASC t1,
			(select gid, max(ver) AS max_ver from TB_PJT_BASC where (PJT_STTS_CD = '완료' or PJT_STTS_CD = '진행중') AND company_id = #{companyId}
			group by gid) t2
			where t1.gid = t2.gid and t1.ver = t2.max_ver
			  and t1.company_id = #{companyId}
			order by t1.gid , t1.ver
		""")
	List<ScheduleDTO> getProjectListByMonth(ScheduleDTO dto);
	
	
	
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
		""")

	int modify(ScheduleDTO dto);

	//일정 삭제
	@Delete("""
		    DELETE FROM schedule
		    WHERE schedule_id = #{scheduleId}
		""")
	int delete(ScheduleDTO dto);
	
    // 알림 발송 대상 조회
//    @Select("""
//    	    SELECT *, '1일전입니다' as msg FROM schedule
//			WHERE alarm = '알림' AND cate = '종일'
//			and ( 
//				(repeat_check = 0 AND #{curr} = DATE_SUB(start_date, INTERVAL 1 DAY))
//			    or
//			    (#{curr} >= DATE_SUB(start_date, INTERVAL 1 DAY) and #{curr} <= DATE_SUB(end_date, INTERVAL 1 DAY) and (
//			        repeat_check = 1 
//			        or
//			        (repeat_check = 2 and weekday(#{curr}) = weekday( DATE_SUB(start_date, INTERVAL 1 DAY))) 
//			        or
//			        (repeat_check = 3 and dayofmonth(#{curr}) = dayofmonth( DATE_SUB(start_date, INTERVAL 1 DAY)) )
//			    )
//			   ) 
//			)
//			union 
//			SELECT *, '1시간전입니다' as msg FROM schedule
//			WHERE alarm = '알림' AND (cate != '종일' or cate is null) AND hour(start_time) = hour(DATE_SUB(#{curr}, INTERVAL 1 HOUR))
//			and (repeat_check = 0 and #{curr} = start_date 
//			    or
//			    (#{curr} >= start_date and #{curr} <= end_date and  (
//			        repeat_check = 1 
//			        or
//			        (repeat_check = 2 and weekday(#{curr}) = weekday( start_date ) ) 
//			        or
//			        (repeat_check = 3 and dayofmonth(#{curr}) = dayofmonth( start_date) )
//			    )
//			   ) 
//			)
//			ORDER BY start_date DESC, start_time DESC
//			LIMIT 10
//    	""")
	@Select("""
			SELECT *, '1일전입니다' as msg FROM schedule
			WHERE alarm = '알림' AND cate = '종일'
			and (
				(repeat_check = 0 AND current_date() = DATE_SUB(start_date, INTERVAL 1 DAY))
			    or
			    (current_date() >= DATE_SUB(start_date, INTERVAL 1 DAY) and now() <= DATE_SUB(end_date, INTERVAL 1 DAY) and (
			        repeat_check = 1
			        or
			        (repeat_check = 2 and weekday(current_date()) = weekday( DATE_SUB(start_date, INTERVAL 1 DAY)))
			        or
			        (repeat_check = 3 and dayofmonth(current_date()) = dayofmonth( DATE_SUB(start_date, INTERVAL 1 DAY)) )
			    )
			   )
			)
			union
			SELECT *, '1시간전입니다' as msg FROM schedule
			WHERE alarm = '알림' AND (cate != '종일' or cate is null) AND hour(start_time) = hour(date_add(now(), INTERVAL 1 HOUR))
			and (repeat_check = 0 and current_date() = start_date
			    or
			    (current_date() >= start_date and current_date() <= end_date and  (
			        repeat_check = 1
			        or
			        (repeat_check = 2 and weekday(current_date()) = weekday( start_date ) )
			        or
			        (repeat_check = 3 and dayofmonth(current_date()) = dayofmonth( start_date) )
			    )
			   )
			)
			ORDER BY start_date DESC, start_time DESC
			LIMIT 10
			""")
    List<ScheduleDTO> getActiveAllDayNotifications(ScheduleDTO schDto);
    


	// 알림 발송 완료 표시
	@Update("""
		    UPDATE schedule
		    SET is_sent = 1,
		        sent_at = NOW()
		    WHERE schedule_id = #{scheduleId}
		""")
	void markNotificationAsSent(int scheduleId);

  
  
}
