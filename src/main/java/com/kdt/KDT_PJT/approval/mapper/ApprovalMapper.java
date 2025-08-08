package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.attend.model.LeaveDTO;


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
	
	@Select("SELECT * FROM Approval_TEST WHERE docId = #{docId}")
	ApprovalDTO selectById(@Param("docId") String docId);
	
	@Delete("DELETE FROM Approval_TEST WHERE docId = #{docId}")
	int deleteById(@Param("docId") String docId);
	
	// <TEST> 테스트용 근태 정보 받아오기
	@Select("SELECT * FROM annual_leave")
	List<LeaveDTO> selectLeaveAll();
	
	@Select("SELECT * FROM annual_leave WHERE leave_id = #{leaveId}")
	LeaveDTO selectLeaveById(int leaveId);
	
}
