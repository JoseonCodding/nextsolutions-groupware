package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;


@Mapper
public interface ApprovalMapper {
	
	/* 글 목록 */
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
	    "   WHERE b.board_id = 1",

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
	    "     '프로젝트부' AS deptName,",
	    "     '박길동' AS writer,",
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

	
	// 글 전체 개수 카운트
	@Select({
	    "<script>",
	    "SELECT COUNT(*) FROM (",
	    // ──────────────── 공지사항
	    "   SELECT CONCAT('notice_', b.post_id) AS docId, '공지사항' AS docType, e.employeeId AS writerId, b.status",
	    "     FROM board_post b",
	    "     LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "    WHERE b.board_id = 1",

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





	
	// 공지사항 + 연차 + 프로젝트 + 근태 DB 끌어오기
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
	    "   WHERE b.board_id = 1",
	    "     <if test='type != null and type != \"\"'>",
	    "       AND b.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND b.status = #{status}",
	    "     </if>",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('leave_', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     CONCAT('연차 사용신청 - ', l.create_reason) AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
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
	    "   <where>",
	    "     l.state_type IS NOT NULL",
	    "     <if test='type != null and type != \"\"'>",
	    "       AND l.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND l.state_type = #{status}",
	    "     </if>",
	    "   </where>",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('project_', p.PJT_SN) AS docId,",
	    "     '프로젝트' AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     '프로젝트부' AS deptName,",
	    "     '박길동' AS writer,",
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
	    "   <where>",
	    "     <if test='type != null and type != \"\"'>",
	    "       p.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND p.PJT_STTS_CD = #{status}",
	    "     </if>",
	    "   </where>",

	    "   UNION ALL",

	    "   SELECT",
	    "     CONCAT('attendance_', a.id) AS docId,",
	    "     '근태' AS docType,",
	    "     '근태 수정 신청' AS title,",
	    "     a.modification_reason AS content,",
	    "     a.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
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
	    "   <where>",
	    "     a.status IS NOT NULL",
	    "     <if test='type != null and type != \"\"'>",
	    "       AND a.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND a.status = #{status}",
	    "     </if>",
	    "   </where>",

	    ") AS all_data",
	    "ORDER BY createdAt DESC",
	    "LIMIT #{offset}, #{size}",
	    "</script>"
	})
	List<ApprovalDTO> approvalData(
		    @Param("offset") int offset,
		    @Param("size") int size,
		    @Param("type") String type,
		    @Param("status") String status
		);


	// 공지사항 + 연차 + 프로젝트 + 근태  DB 총 개수 세기
	@Select({
		  "<script>",
		  "SELECT COUNT(*) FROM (",
		  "  SELECT CONCAT('notice_', b.post_id) AS docId",
		  "    FROM board_post b",
		  "   WHERE b.board_id = 1",
		  "     <if test='type != null and type != \"\"'>",
		  "       AND b.docType = #{type}",
		  "     </if>",
		  "     <if test='status != null and status != \"\"'>",
		  "       AND b.status = #{status}",
		  "     </if>",
		  "  UNION ALL",
		  "  SELECT CONCAT('leave_', l.leave_id) AS docId",
		  "    FROM annual_leave l",
		  "   <where>",
		  "     l.state_type IS NOT NULL",
		  "     <if test='type != null and type != \"\"'>",
		  "       AND l.docType = #{type}",
		  "     </if>",
		  "     <if test='status != null and status != \"\"'>",
		  "       AND l.state_type = #{status}",
		  "     </if>",
		  "   </where>",
		  "  UNION ALL",
		  "  SELECT CONCAT('project_', p.PJT_SN) AS docId",
		  "    FROM TB_PJT_BASC p",
		  "   <where>",
		  "     <if test='type != null and type != \"\"'>",
		  "       p.docType = #{type}",
		  "     </if>",
		  "     <if test='status != null and status != \"\"'>",
		  "       AND p.PJT_STTS_CD = #{status}",
		  "     </if>",
		  "   </where>",
		  "  UNION ALL",
		  "  SELECT CONCAT('attendance_', a.id) AS docId",
		  "    FROM attendance a",
		  "   <where>",
		  "     a.status IS NOT NULL",
		  "     <if test='type != null and type != \"\"'>",
		  "       AND a.docType = #{type}",
		  "     </if>",
		  "     <if test='status != null and status != \"\"'>",
		  "       AND a.status = #{status}",
		  "     </if>",
		  "   </where>",
		  ") AS ALL_DATA",
		  "</script>"
		})
		int approvalCountAll(
		  @Param("type") String type,
		  @Param("status") String status
		);

	
	// 뷰어용 데이터 조회
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
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt",
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1",

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
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt",
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
	    "     '프로젝트부' AS deptName,",
	    "     '박길동' AS writer,",
	    "     p.employeeId AS writerId,",
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN1 AS attachFileUuid, p.ORG_FILE_NM1 AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate, NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime, NULL AS checkOutTime,",
	    "     NULL AS modifiedBy, NULL AS modifiedAt,",
	    "     NULL AS modificationReason, NULL AS timeInout,",
	    "     p.PJT_BGNG_DT AS pjtBgngDt, p.PJT_END_DT AS pjtEndDt",
	    "   FROM TB_PJT_BASC p",

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
	    "     NULL AS pjtBgngDt, NULL AS pjtEndDt",
	    "   FROM attendance a",
	    "   LEFT JOIN employee e ON a.employeeId = e.employeeId",

	    ") AS all_data",
	    "WHERE docId = #{docId}",

	    // ---- 권한 필터 (대표이면 전체 허용)
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

	    // ---- type/status 필터 (선택)
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





	// 결재 종류 별 삭제 메소드 (PK로 비교)
	@Delete("DELETE FROM board_post WHERE post_id = #{id}")
	int deleteNotice(@Param("id") String id);

	@Delete("DELETE FROM annual_leave WHERE leave_id = #{id}")
	int deleteLeave(@Param("id") String id);

	@Delete("DELETE FROM TB_PJT_BASC WHERE PJT_SN = #{id}")
	int deleteProject(@Param("id") String id);
	
	@Delete("DELETE FROM attendance WHERE id = #{id}")
	int deleteAttendance(@Param("id") String id);
	
	
	// 결재 종류 별 수정 메소드
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
	int editLeave(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update("UPDATE TB_PJT_BASC SET docType=#{dto.docType}, PJT_NM=#{dto.title}, content=#{dto.content} WHERE PJT_SN=#{id}")
	int editProject(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	@Update(
			"UPDATE attendance "
			+ "SET docType=#{dto.docType}, "
			+	"modification_reason=#{dto.content}, "
			+	"time_inout=#{dto.timeInout} "
			+ "WHERE "
			+	 "id=#{id}")
	int editAttendance(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	
	// 공지사항 승인&반려 메서드
	@Update("UPDATE board_post SET status=#{status} WHERE post_id=#{id}")
	int updateStatusNotice(@Param("id") String id, @Param("status") String status);
	
	// 연차 승인&반려 메서드
	@Update({
	  "<script>",
	  "UPDATE annual_leave",
	  "SET state_type = #{status}",
	  "<if test=\"status == '완료'\">, leave_type = '사용'</if>",
	  "WHERE leave_id = #{id}",
	  "</script>"
	})
	int updateStatusLeave(@Param("id") String id, @Param("status") String status);
	
	// 프로젝트 승인&반려 메서드
	@Update("UPDATE TB_PJT_BASC SET PJT_STTS_CD=#{status} WHERE PJT_SN=#{id}")
	int updateStatusProject(@Param("id") String id, @Param("status") String status);
	
	// 프로젝트 승인 시, 일정 테이블에 추가되는 메서드
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
	
    // 근태 반려 메서드
    @Update("UPDATE attendance SET status = #{status} WHERE id = #{id}")
    int rejectAttendance(@Param("id") String id, @Param("status") String status);

    // 근태 승인 메서드
    @Update({
        "<script>",
        "UPDATE attendance",
        "SET status = #{status}",
        "<if test='status == \"완료\"'>",
        "  <choose>",
        "    <when test='timeInout == \"출근\"'>",
        "      , check_in_time = DATE_FORMAT(check_in_time, '%Y-%m-%d 09:00:00')",
        "    </when>",
        "    <when test='timeInout == \"퇴근\"'>",
        "      , check_out_time = DATE_FORMAT(check_out_time, '%Y-%m-%d 18:00:00')",
        "    </when>",
        "    <when test='timeInout == \"출퇴근\"'>",
        "      , check_in_time = DATE_FORMAT(check_in_time, '%Y-%m-%d 09:00:00'),",
        "        check_out_time = DATE_FORMAT(check_out_time, '%Y-%m-%d 18:00:00')",
        "    </when>",
        "  </choose>",
        "  , modified_at = NOW()",
        "</if>",
        "WHERE id = #{id}",
        "</script>"
    })
    int approveAttendance(@Param("id") String id, @Param("status") String status, @Param("timeInout") String timeInout);




}
