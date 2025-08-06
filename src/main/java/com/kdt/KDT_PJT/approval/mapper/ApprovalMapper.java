package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

@Mapper
public interface ApprovalMapper {
	
	// 데이터베이스 값 불러오기
	@Select("SELECT * FROM Approval_TEST ORDER BY createdAt DESC")	// 테이블에서 모든 값을 등록일자 기준 내림차순으로 받아옴
	List<ApprovalDTO> selectAllDesc();
	
	
	// 페이지네이션
	@Select("SELECT * FROM Approval_TEST ORDER BY createdAt DESC LIMIT #{offset}, #{size}") // offset:시작위치, size:페이지당 글수 (계산은 컨트롤러에서)
	List<ApprovalDTO> selectPage(@Param("offset") int offset, @Param("size") int size);
	
	// 데이터베이스 전체 행 개수
	@Select("SELECT COUNT(*) FROM Approval_TEST")
	int countAll();
}
