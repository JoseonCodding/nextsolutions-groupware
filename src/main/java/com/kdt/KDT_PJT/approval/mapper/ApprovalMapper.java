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
	
	// 공지사항 + 연차 + 프로젝트 DB 끌어오기 (TEST : docId 만들어서 사용)
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
	    "     NULL AS attachFileOrgName",   
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1",
	    "     <if test='type != null and type != \"\"'>",
	    "       AND b.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND b.status = #{status}",
	    "     </if>",
	    "",
	    "   UNION ALL",
	    "",
	    "   SELECT",
	    "     CONCAT('leave_', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     l.create_reason AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     l.create_date AS createdAt,",
	    "     NULL AS attachFileUuid,",       
	    "     NULL AS attachFileOrgName",     
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "   <where>",
	    "     <if test='type != null and type != \"\"'>",
	    "       l.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND l.state_type = #{status}",
	    "     </if>",
	    "   </where>",
	    "",
	    "   UNION ALL",
	    "",
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
	    "     p.ORG_FILE_NM AS attachFileOrgName",
	    "   FROM TB_PJT_BASC p",
	    "   <where>",
	    "     <if test='type != null and type != \"\"'>",
	    "       p.docType = #{type}",
	    "     </if>",
	    "     <if test='status != null and status != \"\"'>",
	    "       AND p.PJT_STTS_CD = #{status}",
	    "     </if>",
	    "   </where>",
	    "",
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


	
	// 공지사항 + 연차 + 프로젝트 DB 총 개수 세기 (docId:concat 방식으로 임시생성 -> 파라미터로 발사 가능)
	@Select(
	    "<script>"
	    + "SELECT COUNT(*) FROM ("
	    + "  SELECT CONCAT('notice_', b.post_id) AS docId"
	    + "    FROM board_post b"
	    + "   WHERE b.board_id = 1"
	    + "     <if test='type != null and type != \"\"'>"
	    + "       AND b.docType = #{type}"
	    + "     </if> "
	    + "     <if test='status != null and status != \"\"'>"
	    + "       AND b.status = #{status}"
	    + "     </if> "
	    + "  UNION ALL"
	    + "  SELECT CONCAT('leave_', l.leave_id) AS docId"
	    + "    FROM annual_leave l"
	    + "   <where>"
	    + "     <if test='type != null and type != \"\"'>"
	    + "       l.docType = #{type}"
	    + "     </if> "
	    + "     <if test='status != null and status != \"\"'>"
	    + "       AND l.state_type = #{status}"
	    + "     </if> "
	    + "   </where>"
	    + "  UNION ALL"
	    + "  SELECT CONCAT('project_', p.PJT_SN) AS docId"
	    + "    FROM TB_PJT_BASC p"
	    + "   <where>"
	    + "     <if test='type != null and type != \"\"'>"
	    + "       p.docType = #{type}"
	    + "     </if> "
	    + "     <if test='status != null and status != \"\"'>"
	    + "       AND p.PJT_STTS_CD = #{status}"
	    + "     </if> "
	    + "   </where>"
	    + ") AS ALL_DATA"
	    + "</script>"
	)
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
	    "     NULL AS attachFileOrgName",   
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1",
	    "",
	    "   UNION ALL",
	    "",
	    "   SELECT",
	    "     CONCAT('leave_', l.leave_id) AS docId,",
	    "     '연차' AS docType,",
	    "     l.create_reason AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     l.create_date AS createdAt,",
	    "     NULL AS attachFileUuid,",     
	    "     NULL AS attachFileOrgName",   
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "",
	    "   UNION ALL",
	    "",
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
	    "     p.ORG_FILE_NM AS attachFileOrgName",
	    "   FROM TB_PJT_BASC p",
	    ") AS all_data",
	    "WHERE docId = #{docId}",
	    "</script>"
	})
	ApprovalDTO view(@Param("docId") String docId);


	// 결재 종류 별 삭제 메소드 (PK로 비교)
	@Delete("DELETE FROM board_post WHERE post_id = #{id}")
	int deleteNotice(@Param("id") String id);

	@Delete("DELETE FROM annual_leave WHERE leave_id = #{id}")
	int deleteLeave(@Param("id") String id);

	@Delete("DELETE FROM TB_PJT_BASC WHERE PJT_SN = #{id}")
	int deleteProject(@Param("id") String id);

	
	
	// 결재 종류 별 수정 메소드 3개
	@Update("UPDATE board_post SET docType=#{dto.docType}, title=#{dto.title}, content=#{dto.content} WHERE post_id=#{id}")
	int editNotice(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update("UPDATE annual_leave SET docType=#{dto.docType}, create_reason=#{dto.title}, content=#{dto.content} WHERE leave_id=#{id}")
	int editLeave(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	@Update("UPDATE TB_PJT_BASC SET docType=#{dto.docType}, PJT_NM=#{dto.title}, content=#{dto.content} WHERE PJT_SN=#{id}")
	int editProject(@Param("id") String id, @Param("dto") ApprovalDTO dto);

	
	
	// 결재 종류 별 승인or반려 메소드 3개
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


}
