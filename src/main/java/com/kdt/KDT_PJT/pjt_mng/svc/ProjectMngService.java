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
import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
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
      return pjtMngMapper.selectApproverCandidates();
   }

   // 🔹 프로젝트 전체 조회 (기존)
   public List<CmmnMap> getPjtList() {
      return cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtList");
   }

   // 🔹 프로젝트 상세 조회 (기존)
   public CmmnMap getPjtDetail(int pjtSn) {
      System.out.println("getPjtDetail : " + pjtSn);
      return pjtMngMapper.selectPjtDetail(pjtSn);

   }

   // 🔹 프로젝트 등록 (기존)
   public void savePjtProc(CmmnMap params) {

      String queryId = "com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.savePjtProc";
      cmmnDao.insert(queryId, params);
   }

   // 🔹 프로젝트 수정 (기존)
   public void updatePjtProc(CmmnMap params) {

      log.info("svc updatePjtProc >>> ");
      cmmnDao.update("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.updatePjtProc", params);
   }

   // 🔹 키워드 검색 (기존)
   public List<CmmnMap> searchProjectMngList(String keyword) {
      return cmmnDao.selectList("com.kdt.mapper.pjt_mng.PjtMngMapper.searchProjectMngList", keyword);
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

      return cmmnDao.selectList("com.kdt.mapper.pjt_mng.PjtMngMapper.searchProjectPagedList", param);
   }

   // ✅ 전체 카운트 조회 (신규)
   public int countProjectList(String keyword) {
      return cmmnDao.selectOne("com.kdt.mapper.pjt_mng.PjtMngMapper.countProjectList", keyword);
   }

   public PageInfo<CmmnMap> getProjectList(int pageNum, int pageSize, String keyword) {

      PageHelper.startPage(pageNum, pageSize);

      List<CmmnMap> list = cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.searchProjectMngList",
            keyword); // ② 페이징 걸린 상태로 select 실행
      return new PageInfo<>(list); // ③ PageInfo로 래핑
   }

   // DB 전체 개수 조회
   public int getTotalCount() {
      return pjtMngMapper.countAll();
   }

   // DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
   public int getProgressCount() {
      return pjtMngMapper.countProgress();
   }

   // DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
   public int getCompleteCount() {
      return pjtMngMapper.countComplete();
   }

   // DB에서 PJT_STTS_CD가 '대기'인 개수 조회
   public int getPendingCount() {
      return pjtMngMapper.countPending();
   }

   public CmmnMap getProjectById(int pjtSn) {
      // TODO Auto-generated method stub
      return null;
   }

   public CmmnMap getProjectWithApprover(int pjtSn) {
      // TODO Auto-generated method stub
      return null;
   }

   // 내가 참여한 프로젝트 - 메인
   public int getMyProjectCount(String employeeId) {
      return pjtMngMapper.countMyProjects(employeeId);
   }

   public int countMyProjects(String employeeId) {
      // TODO Auto-generated method stub
      return pjtMngMapper.countMyProjects(employeeId);
   }

   
   public int countMyPendingApprovals(String employeeId) {

      if (employeeId == null || employeeId.isBlank())
         return 0;
      return pjtMngMapper.countMyPendingApprovals(employeeId);
   }

// ProjectMngService.java
   public List<CmmnMap> getProjectList(Map<String, Object> param) {
      return pjtMngMapper.selectProjectList(param);
   }

   public int getProjectListCount(Map<String, Object> param) {
      return pjtMngMapper.selectProjectListCount(param);
   }

   public int countMyProjects1(String employeeId) {
      if (employeeId == null || employeeId.isBlank())
         return 0;
      return pjtMngMapper.countMyProjects(employeeId);
   }

   public List<CmmnMap> selectApproverCandidates() {
      // TODO Auto-generated method stub
      return null;
   }

}