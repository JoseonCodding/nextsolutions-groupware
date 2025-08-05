package com.kdt.KDT_PJT.login.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.sample.dto.SampleDTO;

@Service
public class LoginService {

	   @Autowired
	   CmmnDao cmmnDao; // 공통 표준 DAO
	   
	   public void saveProc(CmmnMap params) {      
		      
		      String queryId = "com.kdt.mapper.login.saveProc";    
		      
		      cmmnDao.insert(queryId, params);
		   }

	   public List<CmmnMap> getUserList() {
		   
		    String queryId = "com.kdt.mapper.login.getUserList";    
		   
		return cmmnDao.selectList(queryId);
	   }
	   
	   public void updateUser(CmmnMap params) {
			
		   	String queryId = "com.kdt.mapper.login.updateUser";
			cmmnDao.update(queryId, params);
		}

}
