package com.kdt.KDT_PJT.login.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.login.mapper.LoginMapper;

@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public EmployeeDto login(String employeeId, String password) {
        EmployeeDto user = loginMapper.getUserById(employeeId);
        if (user == null) return null;
        if (!encoder.matches(password, user.getPassword())) return null;
        user.setPassword(null); // 세션에 비밀번호 해시 저장 방지
        return user;
    }
}
