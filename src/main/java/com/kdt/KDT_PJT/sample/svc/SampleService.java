package com.kdt.KDT_PJT.sample.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.sample.dao.SampleDao;

@Service
public class SampleService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	CmmnDao cmmnDao; // 공통 표준 DAO

	@Autowired
	SampleDao sampleDao; // 업무별 DAO

	public void test() {
		log.info("SampleBascSvc.test() >>>>>> 호출됨");
		// cmmnDao.selectOne("com.kdt.mapper.sample.getAllUsers");
		cmmnDao.selectList("com.kdt.mapper.sample.getAllUsers");
	}

	public void test2() {
		log.info("SampleBascSvc.test2() >>>>>> 호출됨");
		sampleDao.selectOne("com.kdt.mapper.sample.getAllUsers");
	}

}
