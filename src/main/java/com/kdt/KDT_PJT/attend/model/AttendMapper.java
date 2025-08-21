package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;

@Mapper
public interface AttendMapper {

   //출근 시간 기록
   @Insert("INSERT INTO attendance (employeeId, check_in_time) VALUES (#{employeeId}, #{checkInTime})")
    void insertAttendance(AttendDTO attendance);
   
   

   //퇴근 시간 기록
   @Update("UPDATE attendance SET check_out_time = #{checkOutTime} WHERE employeeId = #{employeeId} AND DATE(check_in_time) = CURDATE()")
   void updateAttendance(AttendDTO attendance);
   
   //사용자 본인의 출퇴근 기록
   @Select("select * from attendance where employeeId = #{employeeId} ")
   List<AttendDTO> userAttendList(EmployeeDto loginUser);
   
   
   //사용자 본인의 출퇴근 기록 한달 기준 보기  //DTO2 - limit 없는 버전
   @Select("""
		    SELECT * FROM attendance
		    WHERE employeeId = #{employeeId}
		      AND DATE(check_in_time) BETWEEN #{startDay} AND #{endDay}
		    ORDER BY check_in_time
		""")
   List<AttendDTO> userAttendMonthList( AttendDTO2 attendance);
   
   //출퇴근 기록에 연차 날짜 가져오기
   @Select("""
   		select used_date from annual_leave where employeeId = #{employeeId}
   			and state_type ='완료' AND DATE(used_date) BETWEEN #{startDay} AND #{endDay}
		    ORDER BY used_date
   		""")
   List<LeaveDTO> searchLeaveDate(AttendDTO2 attendance);
   
   //출퇴근 기록에 휴무일 가져오기      //date_add(end_date, INTERVAL 1 DAY) : fullcalender가 마지막 날 인지를 못해서 +1일 처리
   @Select("""
   		SELECT title, start_date,  date_add(end_date, INTERVAL 1 DAY) as end_date  FROM schedule 
   		WHERE holiday= '휴무일' and start_date <= #{endDay}  AND  end_date >= #{startDay}
   		""")
   List<ScheduleDTO> searchHoliday(AttendDTO2 attendance);
   
   //오늘 출근 조회용
   @Select("""
         SELECT * FROM attendance 
         WHERE employeeId = #{employeeId} 
           AND DATE(check_in_time) = CURDATE()
         """)
   AttendDTO findTodayAttendance(@Param("employeeId") String employeeId);
   
   // 출퇴근 기록 수정 신청
   @Insert("INSERT INTO attendance (modified_by, modified_at, modification_reason) VALUES (#{modified_at}, #{modification_reason})")
   void attendSave(AttendDTO attendance);

    // 🔹 수정 요청 업데이트 (employeeId + 날짜)
    @Update("""
        UPDATE attendance
        SET
            modified_by = #{modifiedBy},
            modified_at = #{modifiedAt},
            modification_reason = #{modificationReason}
        WHERE employeeId = #{employeeId}
          AND DATE(check_in_time) = #{workDate}
    """)
    int updateAttendModification(@Param("employeeId") String employeeId,
                                 @Param("workDate") String workDate,
                                 @Param("modifiedBy") String modifiedBy,
                                 @Param("modifiedAt") LocalDateTime modifiedAt,
                                 @Param("modificationReason") String modificationReason);
   
   
   //근태 관리자 페이지-출퇴근 시간 조회페이지 검색기능 있는 버전
   @Select("""
          SELECT a.*, e.emp_nm 
          FROM attendance a
          JOIN employee e ON a.employeeId = e.employeeId
          WHERE DATE(a.check_in_time) = CURDATE()
          ORDER BY a.check_in_time DESC
      """)
   List<AttendDTO> getTodayAttendList();

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
	
	


	
//	//근태 관리자 페이지-출퇴근 시간 조회페이지 검색기능 있는 버전
//	@Select("""
//		    SELECT a.*, e.emp_nm 
//		    FROM attendance a
//		    JOIN employee e ON a.employeeId = e.employeeId
//		    WHERE DATE(a.check_in_time) = CURDATE()
//		    ORDER BY a.check_in_time DESC
//		""")
//	List<AttendDTO> getTodayAttendList();
//
//	@Select("""
//	    <script>
//	    SELECT a.*, e.emp_nm 
//	    FROM attendance a
//	    JOIN employee e ON a.employeeId = e.employeeId
//	    WHERE 1=1
//	    <if test="workDate != null and workDate != ''">
//	        AND DATE(a.check_in_time) = #{workDate}
//	    </if>
//	    <if test="empNm != null and empNm != ''">
//	        AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
//	    </if>
//	    <if test="modifiedBy != null and modifiedBy != ''">
//	        AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
//	    </if>
//	    ORDER BY a.check_in_time DESC
//	    </script>
//	""")
//	List<AttendDTO> searchAttendList(@Param("workDate") String workDate,
//	                                 @Param("empNm") String empNm,
//	                                 @Param("modifiedBy") String modifiedBy);

   @Select("""
       <script>
       SELECT a.*, e.emp_nm 
       FROM attendance a
       JOIN employee e ON a.employeeId = e.employeeId
       WHERE 1=1
       <if test="workDate != null and workDate != ''">
           AND DATE(a.check_in_time) = #{workDate}
       </if>
       <if test="empNm != null and empNm != ''">
           AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
       </if>
       <if test="modifiedBy != null and modifiedBy != ''">
           AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
       </if>
       ORDER BY a.check_in_time DESC
       </script>
   """)
   List<AttendDTO> searchAttendList(AttendDTO dto);
   
   
   
 /*  @Select("""
	       <script>
	       SELECT a.*, e.emp_nm 
	       FROM attendance a
	       JOIN employee e ON a.employeeId = e.employeeId
	       WHERE 1=1
	       <if test="workDate != null and workDate != ''">
	           AND DATE(a.check_in_time) = #{workDate}
	       </if>
	       <if test="empNm != null and empNm != ''">
	           AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
	       </if>
	       <if test="modifiedBy != null and modifiedBy != ''">
	           AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
	       </if>
	       ORDER BY a.check_in_time DESC
	       </script>
	   """)
   PageInfo<AttendDTO> searchAttendListPage(AttendDTO dto);*/
   

   // 출퇴근 현황(관리자용)
   @Select("""
	        <script>
	        SELECT a.*, e.emp_nm, e.deptName, mb.emp_nm as  mb_nm 
	        FROM attendance a
	        JOIN employee e ON a.employeeId = e.employeeId
	        left outer JOIN employee mb ON a.modified_by = mb.employeeId
	        WHERE 1=1
	        <if test="workDate != null and workDate != ''">
	            AND a.check_in_time &gt;= STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
	            AND a.check_in_time &lt;  DATE_ADD(STR_TO_DATE(CONCAT(#{workDate}, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 DAY)
	        </if>
	        <if test="empNm != null and empNm != ''">
	            AND e.emp_nm LIKE CONCAT('%', #{empNm}, '%')
	        </if>
	        <if test="modifiedBy != null and modifiedBy != ''">
	            AND a.modified_by LIKE CONCAT('%', #{modifiedBy}, '%')
	        </if>
	        ORDER BY a.check_in_time DESC
	        </script>
	    """)
	    List<AttendDTO> searchAttendListPage(AttendDTO dto);

}
