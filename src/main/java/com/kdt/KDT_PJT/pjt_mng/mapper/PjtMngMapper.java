package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}

