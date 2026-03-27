package com.kdt.KDT_PJT.employee;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    EmployeeMapper employeeMapper;

    @InjectMocks
    EmployeeService employeeService;

    @Test
    @DisplayName("전화번호 중복 존재 → true 반환")
    void existsByPhone_true() {
        when(employeeMapper.countByPhone("01012345678")).thenReturn(1);

        boolean result = employeeService.existsByPhone("01012345678");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("전화번호 중복 없음 → false 반환")
    void existsByPhone_false() {
        when(employeeMapper.countByPhone("01099999999")).thenReturn(0);

        boolean result = employeeService.existsByPhone("01099999999");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사번으로 이름 조회")
    void getEmpNameById() {
        when(employeeMapper.selectEmpNameById("20250001")).thenReturn("홍길동");

        String name = employeeService.getEmpNameById("20250001");

        assertThat(name).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("사원 상세 조회")
    void getDetail() {
        EmployeeDto dto = new EmployeeDto();
        EmployeeDto expected = new EmployeeDto();
        expected.setEmpSeq(1);
        when(employeeMapper.getDetail(dto)).thenReturn(expected);

        EmployeeDto result = employeeService.getDetail(dto);

        assertThat(result.getEmpSeq()).isEqualTo(1);
    }
}
