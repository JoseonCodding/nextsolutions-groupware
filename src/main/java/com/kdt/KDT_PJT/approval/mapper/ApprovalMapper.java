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
	    "     CONCAT('notice-', b.post_id) AS docId,",
	    "     '공지사항' AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     b.created_at AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
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
	    "     CONCAT('leave-', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     '연차 사용 신청' AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     l.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
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
	    "     CONCAT('project-', p.PJT_SN) AS docId,",
	    "     '프로젝트' AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     p.employeeId AS writerId,",
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN1 AS attachFileUuid1,   p.ORG_FILE_NM1 AS attachFileOrgName1,",
	    "     p.ATCH_FILE_SN2 AS attachFileUuid2,   p.ORG_FILE_NM2 AS attachFileOrgName2,",
	    "     p.ATCH_FILE_SN3 AS attachFileUuid3,   p.ORG_FILE_NM3 AS attachFileOrgName3,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason,",
	    "     NULL AS timeInout",
	    "   FROM TB_PJT_BASC p",
	    "   JOIN (",
	    "       SELECT gid, MAX(ver) AS max_ver",
	    "       FROM TB_PJT_BASC",
	    "       GROUP BY gid",
	    "   ) pv ON p.gid = pv.gid AND p.ver = pv.max_ver",
	    "   LEFT JOIN employee e ON p.employeeId = e.employeeId",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('attendance-', a.id) AS docId,",
	    "     '근태' AS docType,",
	    "     CONCAT('근태 수정 신청 - ', COALESCE(DATE_FORMAT(a.check_in_time, '%Y년 %m월 %d일'), '-'), ' ', COALESCE(a.time_inout, '')) AS title,",
	    "     a.modification_reason AS content,",
	    "     a.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     a.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
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
	    "   WHERE a.status IS NOT NULL",
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
	    "   SELECT CONCAT('notice-', b.post_id) AS docId, '공지사항' AS docType, e.employeeId AS writerId, b.status",
	    "     FROM board_post b",
	    "     LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "    WHERE b.board_id = 1 AND b.is_deleted = 0",

	    "   UNION ALL",

	    // ──────────────── 연차
	    "   SELECT CONCAT('leave-', l.leave_id) AS docId, '연차' AS docType, e.employeeId AS writerId, l.state_type AS status",
	    "     FROM annual_leave l",
	    "     LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "    WHERE l.state_type IS NOT NULL",

	    "   UNION ALL",

	    // ──────────────── 프로젝트 (gid별 최신 ver만 카운트)
	    "   SELECT CONCAT('project-', p.PJT_SN) AS docId, '프로젝트' AS docType, p.employeeId AS writerId, p.PJT_STTS_CD AS status",
	    "     FROM TB_PJT_BASC p",
	    "     JOIN (",
	    "         SELECT gid, MAX(ver) AS max_ver",
	    "         FROM TB_PJT_BASC",
	    "         GROUP BY gid",
	    "     ) pv ON p.gid = pv.gid AND p.ver = pv.max_ver",

	    "   UNION ALL",

	    // ──────────────── 근태
	    "   SELECT CONCAT('attendance-', a.id) AS docId, '근태' AS docType, e.employeeId AS writerId, a.status",
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

	
	@Select({
	    "<script>",
	    "SELECT * FROM (",

	    // ───── 공지사항
	    "   SELECT ",
	    "     CONCAT('notice-', b.post_id) AS docId,",
	    "     '공지사항' AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     b.created_at AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "     b.firstSign AS firstSign,",
	    "     b.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '게시판' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName,",
	    "     e.position AS writerPosition,",
	    "     (SELECT position FROM employee WHERE role = '근태' LIMIT 1) AS approverPosition,",
	    "     (SELECT position FROM employee WHERE role = '대표' LIMIT 1) AS managerPosition",
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1 AND b.is_deleted = 0",

	    "   UNION ALL",

	    // ───── 연차
	    "   SELECT",
	    "     CONCAT('leave-', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     '연차 사용 신청' AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     l.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
	    "     l.create_date AS leaveCreateDate, l.used_date AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "     l.firstSign AS firstSign,",
	    "     l.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '근태' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName,",
	    "     e.position AS writerPosition,",
	    "     (SELECT position FROM employee WHERE role = '근태' LIMIT 1) AS approverPosition,",
	    "     (SELECT position FROM employee WHERE role = '대표' LIMIT 1) AS managerPosition",
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "   WHERE l.state_type IS NOT NULL",

	    "   UNION ALL",

	    // ───── 프로젝트
	    "   SELECT",
	    "     CONCAT('project-', p.PJT_SN) AS docId,",
	    "     '프로젝트' AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN1 AS attachFileUuid1,   p.ORG_FILE_NM1 AS attachFileOrgName1,",
	    "     p.ATCH_FILE_SN2 AS attachFileUuid2,  p.ORG_FILE_NM2 AS attachFileOrgName2,",
	    "     p.ATCH_FILE_SN3 AS attachFileUuid3,  p.ORG_FILE_NM3 AS attachFileOrgName3,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     p.PJT_BGNG_DT AS pjtBgngDt, p.PJT_END_DT AS pjtEndDt,",
	    "     p.firstSign AS firstSign,",
	    "     p.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '프로젝트' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName,",
	    "     e.position AS writerPosition,",
	    "     (SELECT position FROM employee WHERE role = '근태' LIMIT 1) AS approverPosition,",
	    "     (SELECT position FROM employee WHERE role = '대표' LIMIT 1) AS managerPosition",
	    "   FROM TB_PJT_BASC p",
	    "   LEFT JOIN employee e ON p.employeeId = e.employeeId",

	    "   UNION ALL",

	    // ───── 근태
	    "   SELECT",
	    "     CONCAT('attendance-', a.id) AS docId,",
	    "     '근태' AS docType,",
	    " 	  CONCAT('근태 수정 신청 - ', COALESCE(DATE_FORMAT(a.check_in_time, '%Y년 %m월 %d일'), '-'), ' ', COALESCE(a.time_inout, '')) AS title,",
	    "     a.modification_reason AS content,",
	    "     a.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     e.employeeId AS writerId,",
	    "     a.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid1, NULL AS attachFileOrgName1,",
	    "     NULL AS attachFileUuid2, NULL AS attachFileOrgName2,",
	    "     NULL AS attachFileUuid3, NULL AS attachFileOrgName3,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     a.check_in_time AS checkInTime, a.check_out_time AS checkOutTime,",
	    "     a.modified_by AS modifiedBy, a.modified_at AS modifiedAt,",
	    "     a.modification_reason AS modificationReason, a.time_inout AS timeInout,",
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt,",
	    "     a.firstSign AS firstSign,",
	    "     a.secondSign AS secondSign,",
	    "     (SELECT emp_nm FROM employee WHERE role = '근태' LIMIT 1) AS approverName,",
	    "     (SELECT emp_nm FROM employee WHERE role = '대표' LIMIT 1) AS managerName,",
	    "     e.position AS writerPosition,",
	    "     (SELECT position FROM employee WHERE role = '근태' LIMIT 1) AS approverPosition,",
	    "     (SELECT position FROM employee WHERE role = '대표' LIMIT 1) AS managerPosition",
	    "   FROM attendance a",
	    "   LEFT JOIN employee e ON a.employeeId = e.employeeId",

	    ") AS all_data",
	    "WHERE docId = #{docId}",

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

	    "</script>"
	})
	ApprovalDTO view(
	    @Param("docId") String docId,
	    @Param("role") String role,
	    @Param("employeeId") String employeeId,
	    @Param("type") String type,
	    @Param("status") String status
	);
	
	// 공지사항 소프트 삭제
	@Update("UPDATE board_post SET is_deleted = 1 WHERE post_id = #{id}")
	int softDeleteNotice(@Param("id") String id);
	
	// 공지사항 수정
	@Update("UPDATE board_post SET docType=#{dto.docType}, title=#{dto.title}, content=#{dto.content} WHERE post_id=#{id}")
	int editNotice(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	// 연차 '삭제' = state_type NULL 처리
	@Update("""
	    UPDATE annual_leave
	    SET state_type = NULL
	    WHERE leave_id = #{id}
	""")
	int softDeleteLeave(@Param("id") String id);

	// 근태 '삭제' = status NULL 처리
	@Update("""
	    UPDATE attendance
	    SET status = NULL
	    WHERE id = #{id}
	""")
	int softDeleteAttendance(@Param("id") String id);


	// 연차 수정
	@Update({
	    "<script>",
	    "UPDATE annual_leave",
	    "SET",
	    "  used_reason   = #{dto.content},",
	    "  used_date     = #{dto.leaveUsedDate}",
	    "WHERE leave_id = #{id}",
	    "</script>"
	})
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
	    "SET status = #{status},",
	    "    firstSign = CASE ",
	    "                 WHEN #{status} IN ('완료','반려') AND firstSign IS NULL THEN NOW()",
	    "                 ELSE firstSign",
	    "               END",
	    "WHERE post_id = #{id}",
	    "  AND status = #{currentStatus}",
	    "</script>"
	})
	int updateStatusNotice(@Param("id") String id,
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

	
//	// 프로젝트 승인 시, 일정 테이블에 추가   --- PJT_SN 필드 URL 만들기 위해서 넣었는데, 현재 사용안하고 이 메서드도 동작막음
//	@Insert("""
//		    INSERT INTO schedule (
//		        title,
//		        start_date,
//		        end_date,
//		        cate,
//		        content,
//		        created_at,
//		        employeeId,
//		        holiday,
//		        PJT_SN
//		    )
//		    SELECT
//		        PJT_NM AS title,
//		        PJT_BGNG_DT AS start_date,
//		        PJT_END_DT AS end_date,
//		        '종일' AS cate,
//		        content AS content,
//		        FRST_REG_DT AS created_at,
//		        employeeId AS employeeId,
//		        '프로젝트' AS holiday,
//		        PJT_SN
//		    FROM TB_PJT_BASC
//		    WHERE PJT_SN = #{id}
//		""")
//	int insertProjectSchedule(@Param("id") String projectId);
	
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
	    "    modified_at = NOW(),",
	    "    modified_by = #{approverId}",   // <- 결재자 기록 저장
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
	    @Param("timeInout") String timeInout,
	    @Param("approverId") String approverId
	);

    
    // 연차 반려
    @Update("""
    	    UPDATE annual_leave
    	    SET state_type = #{status},
    	        firstSign = CASE WHEN #{role} = '근태' AND firstSign IS NULL THEN NOW() ELSE firstSign END,
    	        secondSign = CASE WHEN #{role} = '대표' AND secondSign IS NULL THEN NOW() ELSE secondSign END
    	    WHERE leave_id = #{id}
    	""")
    	int rejectLeave(@Param("id") int id,
    	                @Param("status") String status,
    	                @Param("role") String role);
    
    // 연차 승인
    @Update("""
    	    UPDATE annual_leave
    	    SET state_type = '완료',
    	        leave_type  = '사용',
    	        firstSign   = CASE WHEN #{role} = '근태' AND firstSign IS NULL THEN NOW() ELSE firstSign END,
    	        secondSign  = CASE WHEN #{role} = '대표' AND secondSign IS NULL THEN NOW() ELSE secondSign END
    	    WHERE leave_id = #{id}
    	""")
    	int approveLeave(@Param("id") int id,
    	                 @Param("role") String role);


}
