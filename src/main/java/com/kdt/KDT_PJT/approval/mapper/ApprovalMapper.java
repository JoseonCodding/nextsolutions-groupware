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
	
	@Update("UPDATE Approval_TEST SET status=#{status} WHERE docId=#{docId}")
	int updateStatus(@Param("docId") String docId, @Param("status") String status);
	
	// 공지사항 + 연차 + 프로젝트 DB 끌어오기
	@Select({
	    "<script>",
	    "SELECT * FROM (",
	    "   SELECT ",
	    "     b.docId AS docId,",
	    "     b.docType AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     b.created_at AS createdAt",
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
	    "     l.docId AS docId,",
	    "     l.docType AS docType,",
	    "     l.create_reason AS title,",
	    "     l.used_reason AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     l.create_date AS createdAt",
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
	    "     p.docId AS docId,",
	    "     p.docType AS docType,",
	    "     p.PJT_NM AS title,",
	    "     p.content AS content,",
	    "     p.PJT_STTS_CD AS status,",
	    "     '프로젝트부' AS deptName,",
	    "     '박길동' AS writer,",
	    "     p.FRST_REG_DT AS createdAt",
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

	
	// 공지사항 + 연차 + 프로젝트 DB 총 개수 세기
	@Select(
		    "<script>"
		    + "SELECT COUNT(*) FROM ("
		    + "  SELECT b.docId"
		    + "    FROM board_post b"
		    + "   WHERE b.board_id = 1"
		    + "     <if test='type != null and type != \"\"'>"
		    + "       AND b.docType = #{type}"
		    + "     </if> "
		    + "     <if test='status != null and status != \"\"'>"
		    + "       AND b.status = #{status}"
		    + "     </if> "
		    + "  UNION ALL"
		    + "  SELECT l.docId"
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
		    + "  SELECT p.docId"
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
	
	// 뷰어용 데이터 조회
	@Select({
	    "<script>",
	    "SELECT * FROM (",
	    "   SELECT ",
	    "     b.docId AS docId,",
	    "     b.docType AS docType,",
	    "     b.title AS title,",
	    "     b.content AS content,",
	    "     b.status AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     b.created_at AS createdAt",
	    "   FROM board_post b",
	    "   LEFT JOIN employee e ON b.employee_id = e.employeeId",
	    "   WHERE b.board_id = 1",
	    "",
	    "   UNION ALL",
	    "",
	    "   SELECT",
	    "     l.docId AS docId,",
	    "     l.docType AS docType,",
	    "     l.create_reason AS title,",
	    "     NULL AS content,",
	    "     l.state_type AS status,",
	    "     e.deptName AS deptName,",
	    "     e.emp_nm AS writer,",
	    "     l.create_date AS createdAt",
	    "   FROM annual_leave l",
	    "   LEFT JOIN employee e ON l.employeeId = e.employeeId",
	    "",
	    "   UNION ALL",
	    "",
	    "   SELECT",
	    "     t.docId AS docId,",
	    "     t.docType AS docType,",
	    "     t.PJT_NM AS title,",
	    "     NULL AS content,",
	    "     t.PJT_STTS_CD AS status,",
	    "     '프로젝트부' AS deptName,",
	    "     '박길동' AS writer,",
	    "     t.FRST_REG_DT AS createdAt",
	    "   FROM TB_PJT_BASC t",
	    ") AS all_data",
	    "WHERE docId = #{docId}",
	    "</script>"
	})
	ApprovalDTO view(@Param("docId") String docId);
	
	// 결재 종류 별 삭제 메소드 3개
	@Delete("DELETE FROM board_post WHERE docId = #{docId}")
	int deleteNotice(@Param("docId") String docId);

	@Delete("DELETE FROM annual_leave WHERE docId = #{docId}")
	int deleteLeave(@Param("docId") String docId);

	@Delete("DELETE FROM TB_PJT_BASC WHERE docId = #{docId}")
	int deleteProject(@Param("docId") String docId);
	
	// 결재 종류 별 수정 메소드 3개
	@Update("UPDATE board_post SET docType=#{docType}, title=#{title}, content=#{content} WHERE docId=#{docId}")
	int editNotice(ApprovalDTO dto);

	@Update("UPDATE annual_leave SET docType=#{docType}, create_reason=#{title}, content=#{content} WHERE docId=#{docId}")
	int editLeave(ApprovalDTO dto);

	@Update("UPDATE TB_PJT_BASC SET docType=#{docType}, PJT_NM=#{title}, content=#{content} WHERE docId=#{docId}")
	int editProject(ApprovalDTO dto);


}
