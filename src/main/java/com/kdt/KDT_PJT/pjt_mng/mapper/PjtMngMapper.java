package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

=======
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
>>>>>>> refs/heads/mmmaster
import java.util.List;

@Mapper
public interface PjtMngMapper {
<<<<<<< HEAD
    List<CmmnMap> selectApproverCandidates();
    int updateApprover(Map<String, Object> param);

	
	
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
	
	@Select("select TB_PJT_BASC.* , e1.emp_nm as reg_user, e2.emp_nm as app_user "
			+ "    FROM TB_PJT_BASC, employee e1,  employee e2 "
			+ "    WHERE PJT_SN = #{pjtSn}"
			+ "    and TB_PJT_BASC.employeeid = e1.employeeid "
			+ "    and TB_PJT_BASC.TB_PJT_APR = e2.employeeid ")
    CmmnMap selectPjtDetail(@Param("pjtSn") int pjtSn);



	
	
=======
    // ✅ static 지우고, 몸체(=메서드 본문) 없이 선언만 남긴다
    List<CmmnMap> getApproverList();
>>>>>>> refs/heads/mmmaster
}
