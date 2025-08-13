package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;


@Mapper
public interface ApprovalMapper {
	
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
	    "     NULL AS modificationReason",
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
	    "     NULL AS modificationReason",
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
	    "     p.ATCH_FILE_SN AS attachFileUuid,",
	    "     p.ORG_FILE_NM AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason",
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
	    "     a.modification_reason AS modificationReason",
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


	// 공지사항 + 연차 + 프로젝트 + 근태  DB 총 개수 세기 (docId:concat 방식으로 임시생성 -> 파라미터로 발사 가능)
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

	
	// 뷰어용 데이터 조회 (docId 접두어+PK 방식 적용)
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
	    "     NULL AS modificationReason",
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
	    "     l.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid,",     
	    "     NULL AS attachFileOrgName,",
	    "     l.create_date AS leaveCreateDate,",
	    "     l.used_date AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason",
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
	    "     p.FRST_REG_DT AS createdAt,",
	    "     p.ATCH_FILE_SN AS attachFileUuid,",
	    "     p.ORG_FILE_NM AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     NULL AS checkInTime,",
	    "     NULL AS checkOutTime,",
	    "     NULL AS modifiedBy,",
	    "     NULL AS modifiedAt,",
	    "     NULL AS modificationReason",
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
	    "     a.approval_date AS createdAt,",
	    "     NULL AS attachFileUuid,",
	    "     NULL AS attachFileOrgName,",
	    "     NULL AS leaveCreateDate,",
	    "     NULL AS leaveUsedDate,",
	    "     a.check_in_time AS checkInTime,",
	    "     a.check_out_time AS checkOutTime,",
	    "     a.modified_by AS modifiedBy,",
	    "     a.modified_at AS modifiedAt,",
	    "     a.modification_reason AS modificationReason",
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
	    "WHERE docId = #{docId}",
	    "</script>"
	})
	ApprovalDTO view(
			    @Param("docId") String docId,
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
	
	
	// 결재 종류 별 수정 메소드 4개
	@Update("UPDATE board_post SET docType=#{dto.docType}, title=#{dto.title}, content=#{dto.content} WHERE post_id=#{id}")
	int editNotice(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update({
	    "<script>",
	    "UPDATE annual_leave",
	    "SET docType = #{dto.docType},",
	    "    create_reason = #{dto.title},",
	    "    used_reason = #{dto.content},",  // 여기서 DTO의 content 필드 값이 used_reason에 들어감
	    "    used_date = #{dto.leaveUsedDate}",
	    "WHERE leave_id = #{id}",
	    "</script>"
	})
	int editLeave(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update("UPDATE TB_PJT_BASC SET docType=#{dto.docType}, PJT_NM=#{dto.title}, content=#{dto.content} WHERE PJT_SN=#{id}")
	int editProject(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	@Update("UPDATE attendance SET docType=#{dto.docType}, check_in_time=#{dto.checkInTime}, check_out_time=#{dto.checkOutTime}, modified_by=#{dto.modifiedBy}, modified_at=#{dto.modifiedAt}, modification_reason=#{dto.modificationReason} WHERE id=#{id}")
	int editAttendance(@Param("id") String id, @Param("dto") ApprovalDTO dto);
	
	
	// 결재 종류 별 승인or반려 메소드 4개
	@Update("UPDATE board_post SET status=#{status} WHERE post_id=#{id}")
	int updateStatusNotice(@Param("id") String id, @Param("status") String status);

	@Update({
	  "<script>",
	  "UPDATE annual_leave",
	  "SET state_type = #{status}",
	  "<if test=\"status == '완료'\">, leave_type = '사용'</if>",
	  "WHERE leave_id = #{id}",
	  "</script>"
	})
	int updateStatusLeave(@Param("id") String id, @Param("status") String status);

	@Update("UPDATE TB_PJT_BASC SET PJT_STTS_CD=#{status} WHERE PJT_SN=#{id}")
	int updateStatusProject(@Param("id") String id, @Param("status") String status);
	
	@Update({
	    "<script>",
	    "UPDATE attendance",
	    "SET status = #{status}",
	    "<if test='status == \"완료\"'>",
	    "   , check_in_time = DATE_FORMAT(check_in_time, '%Y-%m-%d 09:00:00')",
	    "   , check_out_time = DATE_FORMAT(check_out_time, '%Y-%m-%d 18:00:00')",
	    "   , modified_at = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul')",
	    "</if>",
	    "WHERE id = #{id}",
	    "</script>"
	})
	int updateStatusAttendance(@Param("id") String id, @Param("status") String status);



}
