package com.kdt.KDT_PJT.ai;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Mapper
public interface MeetingMapper {

    @Insert("""
        INSERT INTO tb_meeting_minutes
        (company_id, employee_id, meet_date, participants, agenda, raw_notes,
         ai_summary, ai_decisions, ai_minutes)
        VALUES
        (#{companyId}, #{employeeId}, #{meetDate}, #{participants}, #{agenda}, #{rawNotes},
         #{aiSummary}, #{aiDecisions}, #{aiMinutes})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "minutesSn")
    int insertMinutes(MeetingMinutesDTO dto);

    @Insert("""
        INSERT INTO tb_meeting_action
        (minutes_sn, task, owner_name, owner_employee_id, due_date)
        VALUES (#{minutesSn}, #{task}, #{ownerName}, #{ownerEmployeeId}, #{dueDate})
    """)
    int insertAction(MeetingActionDTO dto);

    @Select("""
        SELECT minutes_sn, meet_date, participants, agenda, ai_summary, created_at
        FROM tb_meeting_minutes
        WHERE company_id = #{companyId}
        ORDER BY created_at DESC
        LIMIT 30
    """)
    List<CmmnMap> selectRecentMinutes(@Param("companyId") int companyId);

    @Select("SELECT * FROM tb_meeting_action WHERE minutes_sn = #{minutesSn} ORDER BY action_sn")
    List<CmmnMap> selectActionsByMinutesSn(@Param("minutesSn") int minutesSn);

    @Select("SELECT * FROM tb_meeting_minutes WHERE minutes_sn = #{minutesSn}")
    CmmnMap selectMinutesDetail(@Param("minutesSn") int minutesSn);
}
