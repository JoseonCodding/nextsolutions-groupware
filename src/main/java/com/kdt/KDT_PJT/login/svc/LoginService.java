package com.kdt.KDT_PJT.login.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.login.mapper.LoginMapper;
import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private HttpSession session;

    public EmployeeDto login(String employeeId, String password) {
        // 매퍼 파라미터 구성
        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);

        // DB 조회
        EmployeeDto user = loginMapper.getUserByIdAndPassword(params);

        // 로그인 성공 시 세션 저장
//        if (user != null) {
//            session.setAttribute("employeeId", user.getString("employeeId"));
//            session.setAttribute("empNm", user.getString("empNm"));
//        }

        return user;
    }
}
