package com.kdt.KDT_PJT.cmmn.map;

import java.util.Date;
import lombok.Data;

/**
 * 직원 정보를 담는 DTO
 */
@Data
public class EmployeeDto {

    // 기본 키
    private int empSeq;

    // 계정 상태 (1: 활성, 0: 비활성)
    private int active;

    // 로그인 계정 정보
    private String employeeId; // 사번 또는 로그인 ID
    private String password;

    // 개인 정보
    private String empNm;      // 직원 이름
    private String phone;      // 연락처
    private Date birth;        // 생년월일

    // 조직 정보
    private String deptName;   // 부서명
    private String position;   // 직급
    private String role;       // 권한 (ROLE_ADMIN, ROLE_USER 등)

    // 근무 이력
    private Date hireDate;     // 입사일
    private Date resignDate;   // 퇴사일

}
