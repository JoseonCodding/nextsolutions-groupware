package com.kdt.KDT_PJT.attend.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.annotations.Lang;

import java.util.List;

@Mapper
public interface AttendMapper2 {

    @Lang(XMLLanguageDriver.class)   // ✅ 추가
    @Select("""
      <script>
        SELECT
          a.employee_id          AS employeeId,
          e.emp_nm               AS empNm,
          a.check_in_time        AS checkInTime,
          a.check_out_time       AS checkOutTime,
          a.work_hours           AS workHours,
          a.normal_work          AS normalWork,
          a.state_type           AS stateType,
          a.modified_by          AS modifiedBy,
          a.modified_at          AS modifiedAt,
          a.modification_reason  AS modificationReason
        FROM attendance a
        JOIN employee e ON a.employee_id = e.employee_id
        WHERE a.modified_at IS NOT NULL
        <if test="fromDate != null and fromDate != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{fromDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="toDate != null and toDate != ''">
          AND a.check_in_time &lt; DATE_ADD(STR_TO_DATE(CONCAT(#{toDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
        </if>
        <if test="empNm != null and empNm != ''">
          AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
        </if>
        <if test="modifiedBy != null and modifiedBy != ''">
          AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
        </if>
        <if test="stateType != null and stateType != ''">
          AND a.state_type = #{stateType}
        </if>
        ORDER BY a.check_in_time DESC
        LIMIT #{limit} OFFSET #{offset}
      </script>
    """)
    List<AttendDTO> searchAttendListHistoryPaged(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("empNm") String empNm,
            @Param("modifiedBy") String modifiedBy,
            @Param("stateType") String stateType,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Lang(XMLLanguageDriver.class)   // ✅ 추가
    @Select("""
      <script>
        SELECT COUNT(*)
        FROM attendance a
        JOIN employee e ON a.employee_id = e.employee_id
        WHERE a.modified_at IS NOT NULL
        <if test="fromDate != null and fromDate != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{fromDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="toDate != null and toDate != ''">
          AND a.check_in_time &lt; DATE_ADD(STR_TO_DATE(CONCAT(#{toDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
        </if>
        <if test="empNm != null and empNm != ''">
          AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
        </if>
        <if test="modifiedBy != null and modifiedBy != ''">
          AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
        </if>
        <if test="stateType != null and stateType != ''">
          AND a.state_type = #{stateType}
        </if>
      </script>
    """)
    int countAttendListHistory(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("empNm") String empNm,
            @Param("modifiedBy") String modifiedBy,
            @Param("stateType") String stateType
    );
}
