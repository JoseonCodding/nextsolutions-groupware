package com.kdt.KDT_PJT.sample.svc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.sample.dao.SampleDao;
import com.kdt.KDT_PJT.sample.dto.SampleDTO;
import com.kdt.KDT_PJT.sample.vo.SampleVO;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class SampleService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	CmmnDao cmmnDao; // 공통 표준 DAO

	@Autowired
	SampleDao sampleDao; // 업무별 DAO
	
	
	public PageInfo<SampleDTO> getPagedUserList(int pageNum, int pageSize) {
	    // ① 페이징 시작 (ThreadLocal 기반)
	    PageHelper.startPage(pageNum, pageSize);

	    // ② 페이징 걸린 상태로 select 실행
	    List<SampleDTO> userList = sampleDao.selectList("com.kdt.mapper.sample.getUserList");

	    // ③ PageInfo로 래핑
	    return new PageInfo<>(userList);
	}

	public void getAllUsers() {
		log.info("SampleService.getAllUsers() >>>>>> 호출됨");
		cmmnDao.selectList("com.kdt.mapper.sample.getAllUsers");
	}

	public List<SampleVO> getUserList() {
		return sampleDao.selectList("com.kdt.mapper.sample.getUserList");	
	}
	
	public void saveProc(SampleDTO sampleDTO) {		
		
		String queryId = "com.kdt.mapper.sample.saveProc";
		
		sampleDao.insert(queryId, sampleDTO);			
	}


	public void updateProcProc(SampleDTO sampleDTO) {
		sampleDao.update("com.kdt.mapper.sample.updateProc", sampleDTO);
	}

	@Transactional
	public void deleteProc(SampleDTO sampleDTO) {
		sampleDao.delete("com.kdt.mapper.sample.deleteProc", sampleDTO);
	}
}
