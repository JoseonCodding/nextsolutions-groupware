package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.boards.model.BoardDTO;


@Mapper
public interface ApprovalMapper {
	
	/* 테스트 */
	@Select(
			"<script> "
			+ "SELECT * FROM Approval_TEST "
			
			+ " <where> "
			
			+ " <if test='type != null and type != \"\"'> "
			+ " docType = #{type} "
			+ " </if> "
			
			+ " <if test='status != null and status != \"\"'> "
			+ " AND status = #{status} "
			+ " </if> "
			
			+ " </where> "
			
			+ " ORDER BY createdAt DESC "
			+ " LIMIT #{offset}, #{size} "
			
			+ " </script>"
			)
	List<ApprovalDTO> pageData(
							@Param("offset") int offset,
							@Param("size") int size,
							@Param("type") String type,
							@Param("status") String status);
	
	@Select("<script> "
			+ "SELECT COUNT(*) FROM Approval_TEST "
			
			+ " <where> "
			+ 	" <if test='type != null and type != \"\"'> "
			+ 		" docType = #{type} "
			+ 	" </if> "
			+ 	" <if test='status != null and status != \"\"'> "
			+ 		" AND status = #{status} "
			+ 	" </if> "
			+ " </where> "
			+ " </script>"
			)
	int countAll(
			@Param("type") String type,
			@Param("status") String status);
	
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
	
	/* 실습 - 공지사항 게시판 DB 끌어오기 */
	@Select(
		    "<script>"
		    + "SELECT b.*, e.emp_nm, e.deptName "
		    + "FROM board_post b "
		    + "LEFT JOIN employee e ON b.employee_id = e.employeeId "
		    
		    + "<where>"
		    
		    + " b.board_id = 1 "
		    
		    + " <if test='type != null and type != \"\"'> "
		    + " AND b.docType = #{type} "
		    
		    + " </if> "
		    + " <if test='status != null and status != \"\"'> "
		    + " AND b.status = #{status} "
		    + " </if> "
		    
		    + "</where>"
		    
		    + " ORDER BY b.created_at DESC "
		    + " LIMIT #{offset}, #{size} "
		    + "</script>"
			)
	List<BoardDTO> noticeData(
							@Param("offset") int offset,
							@Param("size") int size,
							@Param("type") String type,
							@Param("status") String status);
	
	@Select("<script> "
			+ "SELECT COUNT(*) FROM board_post "
			
			+ " <where> "
			
			+ " board_id = 1 "
			
			+ 	" <if test='type != null and type != \"\"'> "
			+ 		" docType = #{type} "
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
}
