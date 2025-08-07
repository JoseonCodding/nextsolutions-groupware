package com.kdt.KDT_PJT.pjt_mng.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Service
public class ProjcetMngService {

    @Autowired
    private CmmnDao cmmnDao;

    // 🔹 프로젝트 전체 조회 (기존)
    public List<CmmnMap> getPjtList() {
        return cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtList");
    }

    // 🔹 프로젝트 상세 조회 (기존)
    public CmmnMap getPjtDetail(String pjtSn) {
        return cmmnDao.selectOne("com.kdt.mapper.pjt_mng.getPjtDetail", pjtSn);
    }

    // 🔹 프로젝트 등록 (기존)
    public void savePjtProc(CmmnMap params) {
        cmmnDao.insert("com.kdt.mapper.pjt_mng.savePjtProc", params);
    }

    // 🔹 프로젝트 수정 (기존)
    public void updatePjtProc(CmmnMap params) {
        cmmnDao.update("com.kdt.mapper.pjt_mng.updatePjtProc", params);
    }

    // 🔹 키워드 검색 (기존)
    public List<CmmnMap> searchProjectMngList(String keyword) {
        return cmmnDao.selectList("com.kdt.mapper.pjt_mng.searchProjectMngList", keyword);
    }

    // ✅ ✅ ✅ 정렬 + 페이징 + 검색 (신규)
    public List<CmmnMap> searchProjectPagedList(String keyword, String sortType, String order, int offset, int pageSize) {
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", keyword);
        param.put("sortType", sortType);
        param.put("order", order);
        param.put("offset", offset);
        param.put("pageSize", pageSize);

        return cmmnDao.selectList("com.kdt.mapper.pjt_mng.searchProjectPagedList", param);
    }

    // ✅ 전체 카운트 조회 (신규)
    public int countProjectList(String keyword) {
        return cmmnDao.selectOne("com.kdt.mapper.pjt_mng.countProjectList", keyword);
    }
    
    
}
