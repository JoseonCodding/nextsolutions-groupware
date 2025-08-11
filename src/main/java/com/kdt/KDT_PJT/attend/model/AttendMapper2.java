package com.kdt.KDT_PJT.attend.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.annotations.Lang;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendMapper2 {
	
	
	
	 // --- 결재 신청(정정) - 체크박스 분기용 ---

    // IN만 09:00으로 정정
    @Update("""
        UPDATE attendance
        SET
          check_in_time  = STR_TO_DATE(CONCAT(#{workDate}, ' 09:00:00'), '%Y-%m-%d %H:%i:%s'),
          modified_by    = (SELECT emp_nm FROM employee WHERE employeeId = #{employeeId}),
          modified_at    = #{modifiedAt},
          modification_reason = CASE
              WHEN #{title} IS NOT NULL AND #{title} <> '' THEN CONCAT('[', #{title}, '] ', #{modificationReason})
              ELSE #{modificationReason}
          END
        WHERE employeeId = #{employeeId}
          AND DATE(check_in_time) = #{workDate}
          AND state_type = '대기'
    """)
    int fixInByEmpAndDate(@Param("employeeId") String employeeId,
                          @Param("workDate") String workDate,                  // yyyy-MM-dd
                          @Param("modifiedAt") LocalDateTime modifiedAt,
                          @Param("title") String title,
                          @Param("modificationReason") String modificationReason);

    // OUT만 18:00으로 정정
    @Update("""
        UPDATE attendance
        SET
          check_out_time = STR_TO_DATE(CONCAT(#{workDate}, ' 18:00:00'), '%Y-%m-%d %H:%i:%s'),
          modified_by    = (SELECT emp_nm FROM employee WHERE employeeId = #{employeeId}),
          modified_at    = #{modifiedAt},
          modification_reason = CASE
              WHEN #{title} IS NOT NULL AND #{title} <> '' THEN CONCAT('[', #{title}, '] ', #{modificationReason})
              ELSE #{modificationReason}
          END
        WHERE employeeId = #{employeeId}
          AND DATE(check_in_time) = #{workDate}
          AND state_type = '대기'
    """)
    int fixOutByEmpAndDate(@Param("employeeId") String employeeId,
                           @Param("workDate") String workDate,
                           @Param("modifiedAt") LocalDateTime modifiedAt,
                           @Param("title") String title,
                           @Param("modificationReason") String modificationReason);
    
 // 변경이력 검색 (페이징 O)
    @Select("""
      <script>
        SELECT a.*, e.emp_nm
        FROM attendance a
        JOIN employee e ON a.employeeId = e.employeeId
        WHERE a.modified_at IS NOT NULL
        <if test="fromDate != null and fromDate != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{fromDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="toDate != null and toDate != ''">
          AND a.check_in_time &lt;  DATE_ADD(STR_TO_DATE(CONCAT(#{toDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
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

    // 총 건수 (페이징용)
    @Select("""
      <script>
        SELECT COUNT(*)
        FROM attendance a
        JOIN employee e ON a.employeeId = e.employeeId
        WHERE a.modified_at IS NOT NULL
        <if test="fromDate != null and fromDate != ''">
          AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{fromDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
        </if>
        <if test="toDate != null and toDate != ''">
          AND a.check_in_time &lt;  DATE_ADD(STR_TO_DATE(CONCAT(#{toDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
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
