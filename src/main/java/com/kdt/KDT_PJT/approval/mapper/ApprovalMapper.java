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
	
	// 현재 페이지에 들어가는 데이터 베이스 조회
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
	
	// 데이터베이스 전체 행 개수
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
	ApprovalDTO viewById(@Param("docId") String docId);
	
	@Delete("DELETE FROM Approval_TEST " 
			+ " WHERE docId = #{docId}")
	int deleteById(@Param("docId") String docId);
	
	@Update("UPDATE Approval_TEST "
			+ " SET docType=#{docType}, title=#{title}, content=#{content} "
			+ " WHERE docId=#{docId}")
	int updateById(ApprovalDTO dto);
	
	@Select("SELECT * FROM attendance  WHERE employeeId = #{employeeId}")		
	int getAttendInfoList(ApprovalDTO dto);
	
	@Insert("""   
			INSERT INTO Approval_TEST  VALUES
			
			
			""")
	int saveProcs(ApprovalDTO dto);
}
