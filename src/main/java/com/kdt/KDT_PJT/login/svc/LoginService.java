package com.kdt.KDT_PJT.login.svc;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Service
public class LoginService {

    @Autowired
    CmmnDao cmmnDao;

    /** 회원 저장 (emp_seq 자동 생성) */
    public void saveProc(CmmnMap params) {
        String nextSeqQuery = "com.kdt.mapper.login.getNextEmpSeq";
        int nextSeq = (int) cmmnDao.selectOne(nextSeqQuery);
        params.put("emp_seq", nextSeq);

        String insertQuery = "com.kdt.mapper.login.saveProc";
        cmmnDao.insert(insertQuery, params);
    }

    /** 회원 목록 */
    public List<CmmnMap> getUserList() {
        String queryId = "com.kdt.mapper.login.getUserList";
        return cmmnDao.selectList(queryId);
    }

    /** 회원 정보 수정 */
    public void updateUser(CmmnMap params) {
        String queryId = "com.kdt.mapper.login.updateUser";
        cmmnDao.update(queryId, params);
    }

    /** 회원 활성/비활성 토글 */
    public void toggleActive(int empSeq) {
        String queryId = "com.kdt.mapper.login.toggleActive";
        CmmnMap params = new CmmnMap();
        params.put("emp_seq", empSeq);
        cmmnDao.update(queryId, params);
    }

    /** 로그인 검증 */
    public boolean loginCheck(String employeeId, String password) {
        String queryId = "com.kdt.mapper.login.getUserByIdAndPassword";

        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);

        CmmnMap user = cmmnDao.selectOne(queryId, params);
        return user != null;
    }
}
