package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

@Mapper
public interface ApprovalMapper {
	
	// 현재 페이지에 들어가는 데이터 베이스 조회
	@Select("SELECT * FROM Approval_TEST ORDER BY createdAt DESC LIMIT #{offset}, #{size}")
											// 테이블에서 등록일자 기준 내림차순, offset값 부터 size값 까지 받아옴
											// offset:페이지 마다 표시되는 게시글의 시작점, size:페이지당 글수 (계산은 컨트롤러에서)
	List<ApprovalDTO> pageData(@Param("offset") int offset, @Param("size") int size);
	
	// 데이터베이스 전체 행 개수
	@Select("SELECT COUNT(*) FROM Approval_TEST")
	int countAll();
}
