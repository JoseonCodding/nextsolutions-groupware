package com.kdt.KDT_PJT.pjt_mng.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Service
public class ProjcetMngService {
	
    @Autowired
    private CmmnDao cmmnDao;
    

    public List<CmmnMap> getPjtList() {
        return cmmnDao.selectList("com.kdt.mapper.pjt_mng.getPjtList");
    }


	public CmmnMap getPjtDetail(String pjtSn) {
		
		return cmmnDao.selectOne("com.kdt.mapper.pjt_mng.getPjtDetail", pjtSn);
	}


	public void savePjtProc(CmmnMap params) {
		cmmnDao.insert("com.kdt.mapper.pjt_mng.savePjtProc", params);
		
	}


	public void updatePjtProc(CmmnMap params) {
	    cmmnDao.update("com.kdt.mapper.pjt_mng.updatePjtProc", params);
		
	}
    

}
