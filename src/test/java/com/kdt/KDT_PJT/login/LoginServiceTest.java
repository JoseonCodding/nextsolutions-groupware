package com.kdt.KDT_PJT.login;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.login.mapper.LoginMapper;
import com.kdt.KDT_PJT.login.svc.LoginService;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    LoginMapper loginMapper;

    @InjectMocks
    LoginService loginService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private EmployeeDto activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new EmployeeDto();
        activeUser.setEmployeeId("20250001");
        activeUser.setPassword(encoder.encode("1234"));
        activeUser.setActive(1);
    }

    @Test
    @DisplayName("올바른 아이디/비밀번호 → 로그인 성공, 비밀번호 null 처리")
    void login_success() {
        when(loginMapper.getUserById("20250001")).thenReturn(activeUser);

        EmployeeDto result = loginService.login("20250001", "1234");

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo("20250001");
        assertThat(result.getPassword()).isNull(); // 세션 저장 전 비밀번호 제거 확인
    }

    @Test
    @DisplayName("존재하지 않는 아이디 → null 반환")
    void login_unknownId() {
        when(loginMapper.getUserById("unknown")).thenReturn(null);

        EmployeeDto result = loginService.login("unknown", "1234");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("비밀번호 불일치 → null 반환")
    void login_wrongPassword() {
        when(loginMapper.getUserById("20250001")).thenReturn(activeUser);

        EmployeeDto result = loginService.login("20250001", "wrong");

        assertThat(result).isNull();
    }
}
