package com.kdt.KDT_PJT.pjt_mng.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;

@Service
public class ProjectMngService {

   @Autowired
   private CmmnDao cmmnDao;

   @Autowired
   private PjtMngMapper pjtMngMapper;

   // log 사용을 위함
   private final Logger log = LoggerFactory.getLogger(getClass());

   // 결재자 리스트 관련
   public List<CmmnMap> getApproverCandidates() {
      return pjtMngMapper.selectApproverCandidates(CompanyContext.get());
   }

   // 🔹 프로젝트 전체 조회 (기존)
   public List<CmmnMap> getPjtList() {
      return cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getPjtList");
   }

   // 🔹 프로젝트 상세 조회 (기존)
   public CmmnMap getPjtDetail(int pjtSn) {
      return pjtMngMapper.selectPjtDetail(pjtSn);

   }
   
   // 🔹 프로젝트 상세 조회 (기존)
   public CmmnMap getPjtDetailForVersion(String pjtSn) {
      return pjtMngMapper.getPjtDetailForVersion(pjtSn);

   }

   // 🔹 프로젝트 등록 (기존)
   public void savePjtProc(CmmnMap params) {
	   
	      

      String queryId = "com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.savePjtProc";
      cmmnDao.insert(queryId, params);
   }
   
   // 버전관리를 위한 인서트문
   public int savePjtProcForVersion(CmmnMap params) {

	      String queryId = "com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.savePjtProcForVersion";
	      cmmnDao.insert(queryId, params);
	      
	      queryId = "com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.maxPjtSn";
	      return cmmnDao.selectOne(queryId);
	   }
   
   // 새글의 pjt_sn
   public int maxPjtSn() {

	    
	      
	      String queryId = "com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.maxPjtSn";
	      return cmmnDao.selectOne(queryId);
	   }
   

   // 🔹 프로젝트 수정 (기존)
   public void updatePjtProc(CmmnMap params) {

      log.info("svc updatePjtProc >>> ");
      cmmnDao.update("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.updatePjtProc", params);
   }

   // 🔹 키워드 검색 (기존)
   public List<CmmnMap> searchProjectMngList(String keyword) {
      return cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.searchProjectMngList", keyword);
   }
   

   public PageInfo<CmmnMap> getProjectListOrderByWriter(int pageNum, int pageSize, String keyword) {

	      PageHelper.startPage(pageNum, pageSize);
	      log.info("getProjectListOrderByWriter keyword(raw) = {}", keyword);
	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getProjectListOrderByWriter",
	            keyword); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }
   
   public PageInfo<CmmnMap> getProjectListOrderByProject(int pageNum, int pageSize, String keyword) {

	      PageHelper.startPage(pageNum, pageSize);

	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getProjectListOrderByProject",
	            keyword); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }
   
   public PageInfo<CmmnMap> getProjectListOrderByStatus(int pageNum, int pageSize, String keyword) {

	      PageHelper.startPage(pageNum, pageSize);

	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getProjectListOrderByStatus",
	            keyword); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }  


   public PageInfo<CmmnMap> getProjectListMyProject(int pageNum, int pageSize, String keyword) {

	      PageHelper.startPage(pageNum, pageSize);

	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getProjectListMyProject",
	            keyword); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }  
   
   public PageInfo<CmmnMap> getProjectListMyApprovalTodoCount(int pageNum, int pageSize, String keyword) {

	      PageHelper.startPage(pageNum, pageSize);

	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.getProjectListMyProject",
	            keyword); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }  
   
   
   // ✅ ✅ ✅ 정렬 + 페이징 + 검색 (신규)
   public List<CmmnMap> searchProjectPagedList(String keyword, String sortType, String order, int offset,
         int pageSize) {
      Map<String, Object> param = new HashMap<>();
      param.put("keyword", keyword);
      param.put("sortType", sortType);
      param.put("order", order);
      param.put("offset", offset);
      param.put("pageSize", pageSize);

      return cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.searchProjectPagedList", param);
   }
/*
   // ✅ 전체 카운트 조회 (신규)
   public int countProjectList(String keyword) {
      return cmmnDao.selectOne("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.countProjectList", keyword);
   }
  */ 
   

   public PageInfo<CmmnMap> getProjectList(int pageNum, int pageSize, String keyword) {

      PageHelper.startPage(pageNum, pageSize);

      Map<String, Object> param = new HashMap<>();
      param.put("keyword", keyword);
      param.put("companyId", CompanyContext.get());

      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.searchProjectMngList",
    		  param ); // ② 페이징 걸린 상태로 select 실행
      return new PageInfo<>(list); // ③ PageInfo로 래핑
   }


   public PageInfo<CmmnMap> getProjectMyApprovalList(int pageNum, int pageSize, String keyword, String employeeId) {

	      PageHelper.startPage(pageNum, pageSize);

	      Map<String, Object> param = new HashMap<>();
	      param.put("keyword", keyword);
	      param.put("employeeId", employeeId);
	      param.put("pageSize", pageSize);
	      param.put("companyId", CompanyContext.get());

	      List<CmmnMap> list = cmmnDao.selectList("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.searchProjectMngMyApprovalList",
	    		  param ); // ② 페이징 걸린 상태로 select 실행
	      return new PageInfo<>(list); // ③ PageInfo로 래핑
	   }

   // DB 전체 개수 조회
   public int getTotalCount() {
      return pjtMngMapper.countAll(CompanyContext.get());
   }

   // DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
   public int getProgressCount() {
      return pjtMngMapper.countProgress(CompanyContext.get());
   }

   // DB에서 PJT_STTS_CD가 '완료'인 개수 조회
   public int getCompleteCount() {
      return pjtMngMapper.countComplete(CompanyContext.get());
   }

   // DB에서 PJT_STTS_CD가 '대기'인 개수 조회
   public int getPendingCount() {
      return pjtMngMapper.countPending(CompanyContext.get());
   }

   public CmmnMap getProjectWithApprover(int pjtSn) {
      return pjtMngMapper.selectPjtDetail(pjtSn);
   }

   // 내가 참여한 프로젝트 - 메인
   public int getMyProjectCount(String employeeId) {
      return pjtMngMapper.countMyProjects(employeeId, CompanyContext.get());
   }

   public int countMyProjects(String employeeId) {
      return pjtMngMapper.countMyProjects(employeeId, CompanyContext.get());
   }


   public int countMyPendingApprovals(String employeeId) {
      if (employeeId == null || employeeId.isBlank())
         return 0;
      return pjtMngMapper.countMyPendingApprovals(employeeId, CompanyContext.get());
   }
   
   public int countWriter(String keyword) {

	      if (keyword == null || keyword.isBlank())
	         return 0;
	      return pjtMngMapper.countWriter(keyword);
   }
   
   public int countProject(String keyword) {

	      if (keyword == null || keyword.isBlank())
	         return 0;
	      return pjtMngMapper.countProject(keyword);
   }
   
   public int countStatus(String keyword) {	// 진행상태 필터에서 검색 결과의 총 개수 계산
	    if (keyword == null || keyword.isBlank())
	        return 0;
	    return cmmnDao.selectOne("com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper.countStatus", keyword);
	}
   

// ProjectMngService.java
   public List<CmmnMap> getProjectList(Map<String, Object> param) {
      param.put("companyId", CompanyContext.get());
      return pjtMngMapper.selectProjectList(param);
   }

   public int getProjectListCount(Map<String, Object> param) {
      param.put("companyId", CompanyContext.get());
      return pjtMngMapper.selectProjectListCount(param);
   }

   public int countMyProjects1(String employeeId) {
      if (employeeId == null || employeeId.isBlank())
         return 0;
      return pjtMngMapper.countMyProjects(employeeId, CompanyContext.get());
   }

	/**
	 * 결재권자 목록
	 * @return
	 */
	public List<CmmnMap> selectApproverCandidates() {
		return pjtMngMapper.selectApproverCandidates(CompanyContext.get());
	}
//파일 버전 저장에 대한거 

	    // 최신 버전 조회
	    public int findLatestVerByPjtSn(Long pjtSn) {
	        return pjtMngMapper.findLatestVerByPjtSn(pjtSn);
	    }

	    // 새 버전 저장
	    public void insertNewVersion(Map<String, Object> param) {
	        pjtMngMapper.insertNewVersion(param);
	    }

		// 활성사원 목록
	    public List<CmmnMap> getEmployeeList() {
	       return pjtMngMapper.getEmployeeList(CompanyContext.get());
	    }
	}




