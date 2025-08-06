package com.kdt.KDT_PJT.employee.svc;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private CmmnDao cmmnDao;

    /** 사원 목록 조회 */
    public List<CmmnMap> getUserList() {
        return cmmnDao.selectList("com.kdt.mapper.login.getUserList");
    }
    
    /** 신규 사원 등록 */
    public void saveProc(CmmnMap params) {
        // 다음 사원번호 가져오기
        int nextSeq = (int) cmmnDao.selectOne("com.kdt.mapper.login.getNextEmpSeq");
        params.put("emp_seq", nextSeq);

        // insert
        cmmnDao.insert("com.kdt.mapper.login.saveProc", params);
    }

    /** 사원 정보 수정 */
    public void updateUser(CmmnMap params) {
        cmmnDao.update("com.kdt.mapper.login.updateUser", params);
    }

    /** 사원 활성화/비활성화 토글 */
    public void toggleActive(int empSeq) {
        CmmnMap params = new CmmnMap();
        params.put("emp_seq", empSeq);
        cmmnDao.update("com.kdt.mapper.login.toggleActive", params);
    }
}
