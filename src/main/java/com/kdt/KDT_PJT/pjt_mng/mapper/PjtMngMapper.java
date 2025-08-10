package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PjtMngMapper {

	/*
	 * List<Map<String, Object>> searchProjects(@Param("keyword") String
	 * keyword, @Param("sortType") String sortType,
	 * 
	 * @Param("order") String order, @Param("offset") int offset, @Param("pageSize")
	 * int pageSize);
	 * 
	 * int countProjects(@Param("keyword") String keyword);
	 * 
	 * // 프로젝트 리스트 일부만 가져오기 (limit, offset)
	 * 
	 * @Select("SELECT * FROM TB_PJT_BASC ORDER BY PJT_SN DESC LIMIT #{limit} OFFSET #{offset}"
	 * ) List<Map<String, Object>> getProjectList(@Param("limit") int
	 * limit, @Param("offset") int offset);
	 * 
	 * // 총 프로젝트 개수 가져오기 (나중에 필요함) int getProjectCount();
	 */
	
	// DB 전체 개수 조회
	@Select("select count(*) from TB_PJT_BASC")
	int countAll();
	
	// DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
	@Select("select count(*) from TB_PJT_BASC WHERE PJT_STTS_CD = '진행중'")
	int countProgress();
	
	// DB에서 PJT_STTS_CD가 '완료'인 개수 조회
	@Select("select count(*) from TB_PJT_BASC WHERE PJT_STTS_CD = '완료'")
	int countComplete();
	
	// DB에서 PJT_STTS_CD가 '대기'인 개수 조회
	@Select("select count(*) from TB_PJT_BASC WHERE PJT_STTS_CD = '대기'")
	int countPending();
	
	
}

