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
	
	@Select("SELECT * FROM Approval_TEST "
			+ " WHERE docId = #{docId}")
	ApprovalDTO view(@Param("docId") String docId);
	
	@Delete("DELETE FROM Approval_TEST " 
			+ " WHERE docId = #{docId}")
	int delete(@Param("docId") String docId);
	
	@Update("UPDATE Approval_TEST "
			+ " SET docType=#{docType}, title=#{title}, content=#{content} "
			+ " WHERE docId=#{docId}")
	int edit(ApprovalDTO dto);
	
	@Update("UPDATE Approval_TEST SET status=#{status} WHERE docId=#{docId}")
	int updateStatus(@Param("docId") String docId, @Param("status") String status);
	
	/* 게시판 DB 끌어오기 */
	@Select(
		    "<script>"
			
		    + "SELECT b.docType, b.title, b.content, b.status, b.docId, "
		    + " e.emp_nm as writer, e.deptName, "
		    + " b.created_at as createdAt "
		    
		    + "FROM board_post b "
		    + "LEFT JOIN employee e ON b.employee_id = e.employeeId "
		    
		    + "WHERE b.board_id = 1 "
		    
		    + "<if test='type != null and type != \"\"'>"
		    + " AND b.docType = #{type} "
		    + "</if>"
		    
		    + "<if test='status != null and status != \"\"'>"
		    + " AND b.status = #{status} "
		    + "</if>"
		    
		    + "LIMIT #{offset}, #{size}"
		    
		    + "</script>"
		)
	List<ApprovalDTO> noticeData(
							@Param("offset") int offset,
							@Param("size") int size,
							@Param("type") String type,
							@Param("status") String status);
	
	// 공지사항 DB 게시글 수 세기
	@Select(
			"<script> "
			+ "SELECT COUNT(*) FROM board_post "
			
			+ " <where> "
			
			+ " board_id = 1 "
			
			+ 	" <if test='type != null and type != \"\"'> "
			+ 		" AND docType = #{type} "
			+ 	" </if> "
			
			+ 	" <if test='status != null and status != \"\"'> "
			+ 		" AND status = #{status} "
			+ 	" </if> "
			
			+ " </where> "
			
			+ " </script>"
			)
	int noticeCountAll(
					@Param("type") String type,
					@Param("status") String status);
	
	// 연차 DB 데이터 끌어오기
	@Select(
		    "<script>"
		    + "SELECT l.docId, l.docType, l.create_reason as title, l.state_type as status, "
		    + "e.deptName, e.emp_nm as writer, "
		    + "l.create_date as createdAt "
		    
		    + "FROM annual_leave l "
		    + "LEFT JOIN employee e ON l.employeeId = e.employeeId "

		    + "<where>"
		    
		    + " <if test='type != null and type != \"\"'> "
		    + 	" l.docType = #{type} "
		    + " </if> "
		    
		    + " <if test='status != null and status != \"\"'> "
		    + 	" AND l.state_type = #{status} "
		    + " </if> "
		   
		    + "</where>"
		    
		    + "LIMIT #{offset}, #{size}"
		  
		    + "</script>"
			)
		List<ApprovalDTO> leaveData(
							    @Param("offset") int offset,
							    @Param("size") int size,
							    @Param("type") String type,
							    @Param("status") String status);
	
	// 연차 DB 게시글 수 세기
	@Select(
		    "<script>"
		    + "SELECT COUNT(*) FROM annual_leave "
		    
		    + "<where>"
		    
		    + " <if test='type != null and type != \"\"'> "
		    + 	" docType = #{type} "
		    + " </if> "
		    
		    + " <if test='status != null and status != \"\"'> "
		    + 	" AND state_type = #{status} "
		    + " </if> "
		    
		    + "</where>"
		    
		    + "</script>"
			)
		int leaveCountAll(
				    @Param("type") String type,
				    @Param("status") String status);
	
	// 프로젝트 DB 데이터 끌어오기
	@Select(
		    "<script>"
			
		    + "SELECT "
		    + " docId, docType, PJT_NM as title, PJT_STTS_CD as status, "
		    + " '김길동' as writer, '프로젝트부' as deptName, FRST_REG_DT as createdAt "
		    
		    + " FROM TB_PJT_BASC "
		    
		    + "<where>"
		    
		    + " <if test='type != null and type != \"\"'> "
		    + 	" docType = #{type} "
		    + " </if> "

		    + " <if test='status != null and status != \"\"'> "
		    + 	" AND PJT_STTS_CD = #{status} "
		    + " </if> "
		    
		    + "</where>"
		    
		    + "ORDER BY FRST_REG_DT DESC "
		    + "LIMIT #{offset}, #{size}"
		    
		    + "</script>"
			)
	List<ApprovalDTO> projectData(
							    @Param("offset") int offset,
							    @Param("size") int size,
							    @Param("type") String type,
							    @Param("status") String status);
	
	// 프로젝트 DB 게시글 수 세기
	@Select(
		    "<script>"
		    + "SELECT COUNT(*) FROM TB_PJT_BASC "
		    
		    + "<where>"
		    
		    + " <if test='type != null and type != \"\"'> "
		    + 	" docType = #{type} "
		    + " </if> "
		    
		    + " <if test='status != null and status != \"\"'> "
		    + 	" AND PJT_STTS_CD = #{status} "
		    + " </if> "
		    
		    + "</where>"
		    
		    + "</script>"
			)
		int projectCountAll(
				    @Param("type") String type,
				    @Param("status") String status);
}
