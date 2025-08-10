package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AttendMapper {
	
    // --- 출근/퇴근 기록 ---

    // 출근 기록 INSERT
    @Insert("""
        INSERT INTO attendance (employeeId, check_in_time)
        VALUES (#{employeeId}, #{checkInTime})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id") // PK 필요 없으면 삭제 가능
    void insertAttendance(AttendDTO attendance);

    // 오늘자 퇴근 기록 UPDATE
    @Update("""
        UPDATE attendance
        SET check_out_time = #{checkOutTime}
        WHERE employeeId = #{employeeId}
          AND DATE(check_in_time) = CURDATE()
          
    """)
    void updateAttendance(AttendDTO attendance);

    // 본인 출퇴근 목록
    @Select("""
        SELECT * FROM attendance
        WHERE employeeId = #{employeeId}
        ORDER BY check_in_time DESC
    """)
    List<AttendDTO> userAttendList(@Param("employeeId") String employeeId);

    // 관리자용 전체 목록
    @Select("""
        SELECT a.*, e.emp_nm
        FROM attendance a
        JOIN employee e ON a.employeeId = e.employeeId
        ORDER BY a.check_in_time DESC
    """)
    List<AttendDTO> attendList();

    // 오늘 출근 1건 조회
    @Select("""
        SELECT * FROM attendance
        WHERE employeeId = #{employeeId}
          AND DATE(check_in_time) = CURDATE()
    """)
    AttendDTO findTodayAttendance(@Param("employeeId") String employeeId);


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

	/*
	 * //출근 시간 기록
	 * 
	 * @Insert("INSERT INTO attendance (employeeId, check_in_time) VALUES (#{employeeId}, #{checkInTime})"
	 * ) void insertAttendance(AttendDTO attendance);
	 * 
	 * //퇴근 시간 기록
	 * 
	 * @Update("UPDATE attendance SET check_out_time = #{checkOutTime} WHERE employeeId = #{employeeId} AND DATE(check_in_time) = CURDATE()"
	 * ) void updateAttendance(AttendDTO attendance);
	 * 
	 * //사용자 본인의 출퇴근 기록
	 * 
	 * @Select("select * from attendance where employeeId = #{employeeId} ")
	 * List<AttendDTO> userAttendList(@Param("employeeId") String employeeId);
	 * 
	 * 
	 * //모든 사용자의 출퇴근 기록(관리자용) //@Select("select * from attendance ")
	 * 
	 * @Select(""" SELECT a.*, e.emp_nm FROM attendance a JOIN employee e ON
	 * a.employeeId = e.employeeId ORDER BY a.check_in_time DESC """)
	 * List<AttendDTO> attendList();
	 * 
	 * //오늘 출근 조회용
	 * 
	 * @Select(""" SELECT * FROM attendance WHERE employeeId = #{employeeId} AND
	 * DATE(check_in_time) = CURDATE() """) AttendDTO
	 * findTodayAttendance(@Param("employeeId") String employeeId);
	 */
	
	

	//work_hours, is_normal_work 반영
//	@Update("""
//			UPDATE attendance 
//			SET check_out_time = #{checkOutTime}, 
//			    work_hours = #{workHours}, 
//			    is_normal_work = #{normalWork}
//			WHERE employeeId = #{employeeId} 
//			  AND DATE(check_in_time) = CURDATE()
//			""")
//	void updateAttendance(AttendDTO attendance);
	
	//근태 관리자 페이지-출퇴근 시간 조회페이지 검색기능 있는 버전
//	@Select("""
//		    SELECT a.*, e.emp_nm 
//		    FROM attendance a
//		    JOIN employee e ON a.employeeId = e.employeeId
//		    WHERE DATE(a.check_in_time) = CURDATE()
//		    ORDER BY a.check_in_time DESC
//		""")
//		List<AttendDTO> getTodayAttendList();
//
//		@Select("""
//		    <script>
//		    SELECT a.*, e.emp_nm 
//		    FROM attendance a
//		    JOIN employee e ON a.employeeId = e.employeeId
//		    WHERE 1=1
//		    <if test="workDate != null and workDate != ''">
//		        AND DATE(a.check_in_time) = #{workDate}
//		    </if>
//		    <if test="empNm != null and empNm != ''">
//		        AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
//		    </if>
//		    <if test="modifiedBy != null and modifiedBy != ''">
//		        AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
//		    </if>
//		    ORDER BY a.check_in_time DESC
//		    </script>
//		""")
//		List<AttendDTO> searchAttendList(@Param("workDate") String workDate,
//		                                 @Param("empNm") String empNm,
//		                                 @Param("modifiedBy") String modifiedBy);

}
