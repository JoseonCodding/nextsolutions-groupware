package com.kdt.KDT_PJT.pjt_mng.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;



@Service
public class ProjectMngService  {

    @Autowired
    private CmmnDao cmmnDao;
    
    @Autowired
    private PjtMngMapper pjtMngMapper;
    
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());

    // 🔹 프로젝트 전체 조회 (기존)
    public List<CmmnMap> getPjtList() {
        return cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtList");
    }

    // 🔹 프로젝트 상세 조회 (기존)
    public CmmnMap getPjtDetail(String pjtSn) {
    	
    	
    	String queryId = "com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtDetail";
    	
        return cmmnDao.selectOne(queryId, pjtSn);
    }

    // 🔹 프로젝트 등록 (기존)
    public void savePjtProc(CmmnMap params) {
        cmmnDao.insert("com.kdt.mapper.pjt_mng.PjtMngMapper.savePjtProc", params);
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
    public List<CmmnMap> searchProjectPagedList(String keyword, String sortType, String order, int offset, int pageSize) {
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
    
    
    
    
    
 // ✅ 페이지네이션용 목록 조회 (Mapper 방식)
//    public List<Map<String, Object>> getProjectList(int page, int size) {
//        int offset = (page - 1) * size;
//        return pjtMngMapper.getProjectList(size, offset);
//    }
//
//    public int getProjectCount(String keyword) {
//        return pjtMngMapper.getProjectCount();
//    }

}