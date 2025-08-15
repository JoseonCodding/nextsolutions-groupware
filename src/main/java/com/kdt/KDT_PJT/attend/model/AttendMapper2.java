package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AttendMapper2 {

    // --- 결재 신청(정정) - 체크박스 분기용 ---

    // IN만 09:00
    @Update("""
    UPDATE attendance
    SET
      check_in_time       = STR_TO_DATE(CONCAT(#{workDate}, ' 09:00:00'), '%Y-%m-%d %H:%i:%s'),
      modification_reason = #{modificationReason},
      approval_date       = NOW(),
      modified_by         = (SELECT emp_nm FROM employee WHERE employeeId = #{employeeId}),
      `status`            = '대기',
      `time_inout`         = CASE
                              WHEN `time_inout` IN ('퇴근','출퇴근') THEN '출퇴근'
                              ELSE '출근'
                            END
    WHERE employeeId = #{employeeId}
      AND COALESCE(check_in_time, check_out_time) >= STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
      AND COALESCE(check_in_time, check_out_time) <  DATE_ADD(STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
      AND (`status` IS NULL OR `status` = '대기')
    """)
    int fixInByEmpAndDate(@Param("employeeId") String employeeId,
                          @Param("workDate") String workDate,
                          @Param("modificationReason") String modificationReason);

    // OUT만 18:00
    @Update("""
    UPDATE attendance
    SET
      check_out_time      = STR_TO_DATE(CONCAT(#{workDate}, ' 18:00:00'), '%Y-%m-%d %H:%i:%s'),
      modification_reason = #{modificationReason},
      approval_date       = NOW(),
      modified_by         = (SELECT emp_nm FROM employee WHERE employeeId = #{employeeId}),
      `status`            = '대기',
      `time_inout`         = CASE
                              WHEN `time_inout` IN ('출근','출퇴근') THEN '출퇴근'
                              ELSE '퇴근'
                            END
    WHERE employeeId = #{employeeId}
      AND COALESCE(check_in_time, check_out_time) >= STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
      AND COALESCE(check_in_time, check_out_time) <  DATE_ADD(STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
      AND (`status` IS NULL OR `status` = '대기')
    """)
    int fixOutByEmpAndDate(@Param("employeeId") String employeeId,
                           @Param("workDate") String workDate,
                           @Param("modificationReason") String modificationReason);

    // 변경이력 검색 (페이징 O) — limit/offset 있을 때만 적용
    @Select("""
      <script>
        SELECT a.*, e.emp_nm
        FROM attendance a
        JOIN employee e ON a.employeeId = e.employeeId
        WHERE a.modified_at IS NOT NULL
        <if test="startDay != null and startDay != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{startDay}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="endDay != null and endDay != ''">
          AND a.check_in_time &lt; DATE_ADD(STR_TO_DATE(CONCAT(#{endDay}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
        </if>
        <if test="empNm != null and empNm != ''">
          AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
        </if>
        <if test="modifiedBy != null and modifiedBy != ''">
          AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
        </if>
        <if test="status != null and status != ''">
          AND a.status = #{status}
        </if>
        ORDER BY a.check_in_time DESC
        <if test="limit != null and limit &gt; 0">
          LIMIT #{limit}
          <if test="offset != null and offset &gt;= 0">
            OFFSET #{offset}
          </if>
        </if>
      </script>
    """)
    List<AttendDTO> searchAttendListHistoryPaged(AttendDTO dto);

    // 총 건수 (페이징용)
    @Select("""
      <script>
        SELECT COUNT(*)
        FROM attendance a
        JOIN employee e ON a.employeeId = e.employeeId
        WHERE a.modified_at IS NOT NULL
        <if test="startDay != null and startDay != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{startDay}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="endDay != null and endDay != ''">
          AND a.check_in_time &lt; DATE_ADD(STR_TO_DATE(CONCAT(#{endDay}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
        </if>
        <if test="empNm != null and empNm != ''">
          AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
        </if>
        <if test="modifiedBy != null and modifiedBy != ''">
          AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
        </if>
        <if test="status != null and status != ''">
          AND a.status = #{status}
        </if>
      </script>
    """)
    int countAttendListHistory(@Param("startDay") String startDay,
                               @Param("endDay") String endDay,
                               @Param("empNm") String empNm,
                               @Param("modifiedBy") String modifiedBy,
                               @Param("status") String status);
}
