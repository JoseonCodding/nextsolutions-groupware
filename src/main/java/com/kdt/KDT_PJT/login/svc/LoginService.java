package com.kdt.KDT_PJT.login.svc;

import com.kdt.KDT_PJT.login.dto.UserDTO;
import com.kdt.KDT_PJT.login.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    public UserDTO findByEmpNo(String empNo) {
        return loginMapper.findByEmpNo(empNo);
    }
}

