package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;


@Mapper
public interface ApprovalMapper {
	
	/* 프로젝트, 연차, 근태, 공지사항 데이터 조회 -> (전자결재 메인) */
	@Select({
	    "<script>",
	    "SELECT * FROM (",
	    "   SELECT ",
	    "     CONCAT('notice_', b.post_id) AS docId,",
	    "     '공지사항' AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     b.created_at AS createdAt,",
	    "     NULL AS attachFileUuid,",
	    "     NULL AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason,",
	    "     NULL AS timeInout",
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1 AND b.is_deleted = 0",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('leave_', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     CONCAT('연차 사용신청 - ', l.create_reason) AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     l.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid,",
	    "     NULL AS attachFileOrgName,",
	    "     l.create_date AS leaveCreateDate,",
	    "     l.used_date AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason,",
	    "     NULL AS timeInout",
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "   WHERE l.state_type IS NOT NULL",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('project_', p.PJT_SN) AS docId,",
	    "     '프로젝트' AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     p.employeeId AS writerId,",
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN1 AS attachFileUuid,",
	    "     p.ORG_FILE_NM1 AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason,",
	    "     NULL AS timeInout",
	    "   FROM TB_PJT_BASC p",
	    "   LEFT JOIN employee e ON p.employeeId = e.employeeId",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('attendance_', a.id) AS docId,",
	    "     '근태' AS docType,",
	    "     '근태 수정 신청' AS title,",
	    "     a.modification_reason AS content,",
	    "     a.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     a.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid,",
	    "     NULL AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     a.check_in_time AS checkInTime,",
	    "     a.check_out_time AS checkOutTime,",
	    "     a.modified_by AS modifiedBy,",
	    "     a.modified_at AS modifiedAt,",
	    "     a.modification_reason AS modificationReason,",
	    "     a.time_inout AS timeInout",
	    "   FROM attendance a",
	    "   LEFT JOIN employee e ON a.employeeId = e.employeeId",
	    ") AS all_data",
	    "WHERE 1=1",

	    "<choose>",
	    "  <when test='role != \"대표\"'>",
	    "    <choose>",
	    "      <when test='role == \"프로젝트\"'>",
	    "        AND (docType = '프로젝트' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"근태\"'>",
	    "        AND ((docType = '근태' OR docType = '연차') OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"게시판\"'>",
	    "        AND (docType = '공지사항' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <otherwise>",
	    "        AND writerId = #{employeeId}",
	    "      </otherwise>",
	    "    </choose>",
	    "  </when>",
	    "</choose>",

	    "<if test='type != null and type != \"\"'>",
	    "  AND docType = #{type}",
	    "</if>",
	    "<if test='status != null and status != \"\"'>",
	    "  AND status = #{status}",
	    "</if>",

	    "ORDER BY createdAt DESC",
	    "LIMIT #{offset}, #{size}",
	    "</script>"
	})
	List<ApprovalDTO> approvalDataByRole(
		    @Param("offset") int offset,
		    @Param("size") int size,
		    @Param("role") String role,
		    @Param("type") String type,
		    @Param("status") String status,
		    @Param("employeeId") String employeeId
		);

	
	/* 글 전체 개수 카운트 -> (페이지네이션) */
	@Select({
	    "<script>",
	    "SELECT COUNT(*) FROM (",
	    // ──────────────── 공지사항
	    "   SELECT CONCAT('notice_', b.post_id) AS docId, '공지사항' AS docType, e.employeeId AS writerId, b.status",
	    "     FROM board_post b",
	    "     LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "    WHERE b.board_id = 1 AND b.is_deleted = 0",

	    "   UNION ALL",

	    // ──────────────── 연차
	    "   SELECT CONCAT('leave_', l.leave_id) AS docId, '연차' AS docType, e.employeeId AS writerId, l.state_type AS status",
	    "     FROM annual_leave l",
	    "     LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "    WHERE l.state_type IS NOT NULL",

	    "   UNION ALL",

	    // ──────────────── 프로젝트
	    "   SELECT CONCAT('project_', p.PJT_SN) AS docId, '프로젝트' AS docType, p.employeeId AS writerId, p.PJT_STTS_CD AS status",
	    "     FROM TB_PJT_BASC p",

	    "   UNION ALL",

	    // ──────────────── 근태
	    "   SELECT CONCAT('attendance_', a.id) AS docId, '근태' AS docType, e.employeeId AS writerId, a.status",
	    "     FROM attendance a",
	    "     LEFT JOIN employee e ON a.employeeId = e.employeeId",
	    "    WHERE a.status IS NOT NULL",
	    ") AS all_data",
	    "WHERE 1=1",

	    // --- role + 내글 조건 ---
	    "<choose>",
	    "  <when test='role != \"대표\"'>",
	    "    <choose>",
	    "      <when test='role == \"프로젝트\"'>",
	    "        AND (docType = '프로젝트' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"근태\"'>",
	    "        AND (docType IN ('근태','연차') OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"게시판\"'>",
	    "        AND (docType = '공지사항' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <otherwise>",
	    "        AND writerId = #{employeeId}",
	    "      </otherwise>",
	    "    </choose>",
	    "  </when>",
	    "</choose>",

	    // --- type/status 필터 ---
	    "<if test='type != null and type != \"\"'>",
	    "  AND docType = #{type}",
	    "</if>",
	    "<if test='status != null and status != \"\"'>",
	    "  AND status = #{status}",
	    "</if>",

	    "</script>"
	})
	int approvalCountByRole(
	    @Param("role") String role,
	    @Param("type") String type,
	    @Param("status") String status,
	    @Param("employeeId") String employeeId
	);
	
	/* 게시글 데이터 조회 -> (전자결재 뷰어) */
	@Select({
	    "<script>",
	    "SELECT * FROM (",

	    // ───── 공지사항
	    "   SELECT ",
	    "     CONCAT('notice_', b.post_id) AS docId,",
	    "     '공지사항' AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     b.created_at AS createdAt,",
	    "     NULL AS attachFileUuid, NULL AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "	  b.firstSign AS firstSign,",
	    "	  b.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '게시판' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName",
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1 AND b.is_deleted = 0",

	    "   UNION ALL",

	    // ───── 연차
	    "   SELECT",
	    "     CONCAT('leave_', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     CONCAT('연차 사용신청 - ', l.create_reason) AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     l.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid, NULL AS attachFileOrgName,",
	    "     l.create_date AS leaveCreateDate, l.used_date AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "	  l.firstSign AS firstSign,",
	    "	  l.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '근태' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName",
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "   WHERE l.state_type IS NOT NULL",

	    "   UNION ALL",

	    // ───── 프로젝트
	    "   SELECT",
	    "     CONCAT('project_', p.PJT_SN) AS docId,",
	    "     '프로젝트' AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN1 AS attachFileUuid, p.ORG_FILE_NM1 AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     p.PJT_BGNG_DT AS pjtBgngDt, p.PJT_END_DT AS pjtEndDt,",
	    "	  p.firstSign AS firstSign,",
	    "	  p.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '프로젝트' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName",
	    "   FROM TB_PJT_BASC p",
	    "   LEFT JOIN employee e ON p.employeeId = e.employeeId",

	    "   UNION ALL",

	    // ───── 근태
	    "   SELECT",
	    "     CONCAT('attendance_', a.id) AS docId,",
	    "     '근태' AS docType,",
	    "     '근태 수정 신청' AS title,",
	    "     a.modification_reason AS content,",
	    "     a.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     a.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid, NULL AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     a.check_in_time AS checkInTime, a.check_out_time AS checkOutTime,",
	    "     a.modified_by AS modifiedBy, a.modified_at AS modifiedAt,",
	    "     a.modification_reason AS modificationReason, a.time_inout AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "	  a.firstSign AS firstSign,",
	    "	  a.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '근태' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName",
	    "   FROM attendance a",
	    "   LEFT JOIN employee e ON a.employeeId = e.employeeId",

	    ") AS all_data",
	    "WHERE docId = #{docId}",

	    // ---- 권한 필터
	    "<choose>",
	    "  <when test='role != \"대표\"'>",
	    "    <choose>",
	    "      <when test='role == \"프로젝트\"'>",
	    "        AND (docType = '프로젝트' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"근태\"'>",
	    "        AND ((docType = '근태' OR docType = '연차') OR writerId = #{employeeId})",
	    "      </when>",
	    "      <when test='role == \"게시판\"'>",
	    "        AND (docType = '공지사항' OR writerId = #{employeeId})",
	    "      </when>",
	    "      <otherwise>",
	    "        AND writerId = #{employeeId}",
	    "      </otherwise>",
	    "    </choose>",
	    "  </when>",
	    "</choose>",

	    // ---- type/status 필터
	    "<if test='type != null and type != \"\"'>",
	    "  AND docType = #{type}",
	    "</if>",
	    "<if test='status != null and status != \"\"'>",
	    "  AND status = #{status}",
	    "</if>",

	    "</script>"
	})
	ApprovalDTO view(
	    @Param("docId") String docId,
	    @Param("role") String role,
	    @Param("employeeId") String employeeId,
	    @Param("type") String type,
	    @Param("status") String status
	);


//	// 이제 삭제 기능은 없습니다~ (is_deleted를 1로 바꾸는 소프트 삭제만 남았어요~)
//	@Delete("DELETE FROM board_post WHERE post_id = #{id}")
//	int deleteNotice(@Param("id") String id);
//
//	@Delete("DELETE FROM annual_leave WHERE leave_id = #{id}")
//	int deleteLeave(@Param("id") String id);
//
//	@Delete("DELETE FROM TB_PJT_BASC WHERE PJT_SN = #{id}")
//	int deleteProject(@Param("id") String id);
//	
//	@Delete("DELETE FROM attendance WHERE id = #{id}")
//	int deleteAttendance(@Param("id") String id);
	
	// 공지사항 소프트 삭제
	@Update("UPDATE board_post SET is_deleted = 1 WHERE post_id = #{id}")
	int softDeleteNotice(@Param("id") String id);
	
	// 공지사항 수정
	@Update("UPDATE board_post SET docType=#{dto.docType}, title=#{dto.title}, content=#{dto.content} WHERE post_id=#{id}")
	int editNotice(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update({
	    "<script>",
	    "UPDATE annual_leave",
	    "SET docType = #{dto.docType},",
	    "    create_reason = #{dto.title},",
	    "    used_reason = #{dto.content},",
	    "    used_date = #{dto.leaveUsedDate}",
	    "WHERE leave_id = #{id}",
	    "</script>"
	})
	
	// 연차 수정
	int editLeave(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	// 프로젝트 수정
	@Update("UPDATE TB_PJT_BASC SET docType=#{dto.docType}, PJT_NM=#{dto.title}, content=#{dto.content} WHERE PJT_SN=#{id}")
	int editProject(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	// 근태 수정
	@Update(
			"UPDATE attendance "
			+ "SET docType=#{dto.docType}, "
			+	"modification_reason=#{dto.content}, "
			+	"time_inout=#{dto.timeInout} "
			+ "WHERE "
			+	 "id=#{id}")
	int editAttendance(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	
	// 공지사항 승인 & 반려
	@Update({
	    "<script>",
	    "UPDATE board_post",
	    "SET status = #{status}",
	    "<choose>",
	    "  <when test='status == \"진행중\"'> , firstSign = NOW() </when>",
	    "  <when test='status == \"완료\"'> , secondSign = NOW() </when>",
	    "  <when test='status == \"반려\"'>",
	    "    <if test='currentStatus == \"대기\"'> , firstSign = NOW() </if>",
	    "    <if test='currentStatus == \"진행중\"'> , secondSign = NOW() </if>",
	    "  </when>",
	    "</choose>",
	    "WHERE post_id = #{id}",
	    "</script>"
	})
	int updateStatusNotice(@Param("id") String id,
	                       @Param("status") String status,
	                       @Param("currentStatus") String currentStatus);
	
	// 연차 승인 & 반려
	@Update({
	    "<script>",
	    "UPDATE annual_leave",
	    "SET state_type = #{status}",
	    "<if test='status == \"완료\"'> , leave_type = '사용' </if>",
	    "<choose>",
	    "  <when test='status == \"진행중\"'> , firstSign = NOW() </when>",
	    "  <when test='status == \"완료\"'> , secondSign = NOW() </when>",
	    "  <when test='status == \"반려\"'>",
	    "    <if test='currentStatus == \"대기\"'> , firstSign = NOW() </if>",
	    "    <if test='currentStatus == \"진행중\"'> , secondSign = NOW() </if>",
	    "  </when>",
	    "</choose>",
	    "WHERE leave_id = #{id}",
	    "</script>"
	})
	int updateStatusLeave(@Param("id") String id,
	                      @Param("status") String status,
	                      @Param("currentStatus") String currentStatus);
	
	// 프로젝트 승인 & 반려
	@Update({
	    "<script>",
	    "UPDATE TB_PJT_BASC",
	    "SET PJT_STTS_CD = #{status}",
	    "<choose>",
	    "  <when test='status == \"진행중\"'> , firstSign = NOW() </when>",
	    "  <when test='status == \"완료\"'> , secondSign = NOW() </when>",
	    "  <when test='status == \"반려\"'>",
	    "    <if test='currentStatus == \"대기\"'> , firstSign = NOW() </if>",
	    "    <if test='currentStatus == \"진행중\"'> , secondSign = NOW() </if>",
	    "  </when>",
	    "</choose>",
	    "WHERE PJT_SN = #{id}",
	    "</script>"
	})
	int updateStatusProject(@Param("id") String id,
	                        @Param("status") String status,
	                        @Param("currentStatus") String currentStatus);

	
	// 프로젝트 승인 시, 일정 테이블에 추가
	@Insert("""
		    INSERT INTO schedule (
		        title,
		        start_date,
		        end_date,
		        cate,
		        content,
		        created_at,
		        employeeId,
		        holiday
		    )
		    SELECT
		        PJT_NM AS title,
		        PJT_BGNG_DT AS start_date,
		        PJT_END_DT AS end_date,
		        '종일' AS cate,
		        content AS content,
		        FRST_REG_DT AS created_at,
		        employeeId AS employeeId,
		        '프로젝트' AS holiday
		    FROM TB_PJT_BASC
		    WHERE PJT_SN = #{id}
		""")
	int insertProjectSchedule(@Param("id") String projectId);
	
    // 근태 반려
	@Update("""
		    UPDATE attendance
		    SET status = #{status},
		        firstSign = CASE
		                      WHEN #{role} = '근태' AND firstSign IS NULL THEN NOW()
		                      ELSE firstSign
		                    END,
		        secondSign = CASE
		                       WHEN #{role} = '대표' AND secondSign IS NULL THEN NOW()
		                       ELSE secondSign
		                     END
		    WHERE id = #{id}
		""")
		int rejectAttendance(
		    @Param("id") int id,
		    @Param("status") String status,
		    @Param("role") String role
		);
    
    // 근태 승인
    @Update({
        "<script>",
        "UPDATE attendance",
        "SET status = '완료',",
        "    secondSign = NOW(),",
        "    modified_at = NOW()",
        "<choose>",
        "  <when test='timeInout == \"출근\"'>",
        "    , check_in_time = DATE_FORMAT(check_in_time, '%Y-%m-%d 09:00:00')",
        "  </when>",
        "  <when test='timeInout == \"퇴근\"'>",
        "    , check_out_time = DATE_FORMAT(check_out_time, '%Y-%m-%d 18:00:00')",
        "  </when>",
        "  <when test='timeInout == \"출퇴근\"'>",
        "    , check_in_time = DATE_FORMAT(check_in_time, '%Y-%m-%d 09:00:00'),",
        "      check_out_time = DATE_FORMAT(check_out_time, '%Y-%m-%d 18:00:00')",
        "  </when>",
        "</choose>",
        "WHERE id = #{id}",
        "</script>"
    })
    int approveAttendance(
		    	    @Param("id") String id,
		    	    @Param("timeInout") String timeInout);




}
