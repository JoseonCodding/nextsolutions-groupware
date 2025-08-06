package com.kdt.KDT_PJT.login.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.login.mapper.LoginMapper;

@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    public CmmnMap login(String employeeId, String password) {
        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);
        return loginMapper.getUserByIdAndPassword(params);
    }
}
