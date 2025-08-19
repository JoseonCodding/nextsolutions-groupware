package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;

import lombok.RequiredArgsConstructor;


import java.util.List;
import java.util.Map;

@Mapper
public interface PjtMngMapper {
	
	
	@Select(" SELECT * FROM team2_db.employee ")
    List<CmmnMap> selectApproverCandidates();
	
    List<CmmnMap> selectProjectList(@Param("param") Map<String, Object> param);
    List<Map<String, Object>> searchProjectMngList(Map<String, Object> param);

       
  //메인 : 내가 참여한 프로젝트 관련
    
         
    @Select(" SELECT COUNT(*) FROM TB_PJT_BASC WHERE (USE_YN IS NULL OR USE_YN = 'Y') AND employeeId = #{employeeId}")
    int countMyProjects(@Param("employeeId") String employeeId);
    
    
    int selectProjectListCount(@Param("param") Map<String, Object> param);
    int countMyProjects1(@Param("employeeId") String employeeId);
    
    
    @Select("    SELECT COUNT(*)\r\n"
    		+ "    FROM TB_PJT_BASC\r\n"
    		+ "    WHERE (USE_YN IS NULL OR USE_YN = 'Y') "
    		+ "      AND TB_PJT_APR = #{employeeId}")
    int countMyPendingApprovals(@Param("employeeId") String employeeId);
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
   
   @Select(" select "
   			+ " t2.* "
   			+ ", (SELECT emp_nm from employee where 1=1 and employeeId = t2.employeeId) as reg_user"
   			+ ", (SELECT emp_nm from employee where 1=1 and employeeId = t2.employeeId) as app_user"
   			+ "    FROM TB_PJT_BASC t2"
   			+ "    WHERE PJT_SN = #{pjtSn}"
		   )
    CmmnMap selectPjtDetail(@Param("pjtSn") int pjtSn);

   
   @Select("select TB_PJT_BASC.* , e1.emp_nm as reg_user, e2.emp_nm as app_user "
	         + "    FROM TB_PJT_BASC, employee e1,  employee e2 "
	         + "    WHERE PJT_SN = #{pjtSn}"
	         + "    and TB_PJT_BASC.employeeid = e1.employeeid "
	         + "    and TB_PJT_BASC.TB_PJT_APR = e2.employeeid ")
	CmmnMap getPjtDetailForVersion(@Param("pjtSn") String pjtSn);

   // 파일 버전관리 
   @Mapper
   public interface ProjectMngMapper {
       int findLatestVerByPjtSn(Long pjtSn);
       int insertNewVersion(Map<String, Object> param);
   }

   // 이름 단건조회  api
   @Service
   @RequiredArgsConstructor
   public class EmployeeService {
       private final EmployeeMapper employeeMapper;

       public String getEmpNameById(String employeeId) {
    	    return employeeMapper.selectEmpNameById(employeeId);
    	}

   }

   @Mapper
   public interface EmployeeMapper {
       String selectEmpNameById(String employeeId);
   }
   
   


}

