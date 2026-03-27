package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;


import java.util.List;
import java.util.Map;

@Mapper
public interface PjtMngMapper {
	
	
    List<CmmnMap> selectApproverCandidates(@Param("companyId") Integer companyId);
	
	
	
	
    List<CmmnMap> selectProjectList(@Param("param") Map<String, Object> param);
    List<Map<String, Object>> searchProjectMngList(Map<String, Object> param);

       
  //메인 : 내가 참여한 프로젝트 관련
    
         
    int countMyProjects(@Param("employeeId") String employeeId, @Param("companyId") Integer companyId);
    
    
    int selectProjectListCount(@Param("param") Map<String, Object> param);
    int countMyProjects1(@Param("employeeId") String employeeId);
    
    
    int countMyPendingApprovals(@Param("employeeId") String employeeId, @Param("companyId") Integer companyId);
    int updateApprover(Map<String, Object> param);


   
   
   // DB 전체 개수 조회 + 버전 제일 높은 놈
   @Select("""
       <script>
       SELECT count(*) FROM VIEW_PJT_BASC
       <if test="companyId != null">WHERE company_id = #{companyId}</if>
       </script>
       """)
   int countAll(@Param("companyId") Integer companyId);

   // DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
   @Select("""
       <script>
       SELECT count(*) FROM VIEW_PJT_BASC
       WHERE PJT_STTS_CD = '진행중'
       <if test="companyId != null">AND company_id = #{companyId}</if>
       </script>
       """)
   int countProgress(@Param("companyId") Integer companyId);

   // DB에서 PJT_STTS_CD가 '완료'인 개수 조회
   @Select("""
       <script>
       SELECT count(*) FROM VIEW_PJT_BASC
       WHERE PJT_STTS_CD = '완료'
       <if test="companyId != null">AND company_id = #{companyId}</if>
       </script>
       """)
   int countComplete(@Param("companyId") Integer companyId);

   // DB에서 PJT_STTS_CD가 '대기'인 개수 조회
   @Select("""
       <script>
       SELECT count(*) FROM VIEW_PJT_BASC
       WHERE PJT_STTS_CD = '대기'
       <if test="companyId != null">AND company_id = #{companyId}</if>
       </script>
       """)
   int countPending(@Param("companyId") Integer companyId);
   
   
   
// DB에서 작성자 조회 개수 조회
   @Select("""
   		<script>
   		SELECT count(*)
   		FROM  VIEW_PJT_BASC t2
   		 <if test="keyword != null and keyword != ''">
   		WHERE t2.employeeId IN (
	      SELECT e.employeeId
	      FROM employee e
	      WHERE e.emp_nm LIKE CONCAT('%', #{keyword}, '%')  )
	      </if>	
	      </script>	
   		""")
   int countWriter(String keyword);
   
   
// DB에서 작성자 조회 개수 조회
   @Select("""
   		<script>
   		SELECT count(*)
   		FROM  VIEW_PJT_BASC t2
   		<if test="keyword != null and keyword != ''">
   		  WHERE  (PJT_NM LIKE CONCAT('%', #{keyword}, '%'))          
   		</if>
	    </script>	
   		""")
   int countProject(String keyword);
   
   
   
   
   @Select(" select "
   			+ " t2.* "
   			+ ", (SELECT emp_nm from employee where 1=1 and employeeId = t2.employeeId) as reg_user"
   			+ ", (SELECT emp_nm from employee where 1=1 and employeeId = t2.TB_PJT_APR) as app_user"
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
   
   
   @Select("""
       <script>
       SELECT * FROM employee WHERE active = 1
       <if test="companyId != null">AND company_id = #{companyId}</if>
       </script>
       """)
   List<CmmnMap> getEmployeeList(@Param("companyId") Integer companyId);

   // 파일 버전관리
   int findLatestVerByPjtSn(Long pjtSn);
   int insertNewVersion(Map<String, Object> param);

}

