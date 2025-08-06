package com.kdt.KDT_PJT.login.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String empNo;      // 사번
    private String empName;    // 이름
    private String deptName;   // 부서명
    private String position;   // 직위
    private String password;   // 비밀번호
    private String role;       // 권한
    private String status;     // 계정 상태 (ACTIVE, SUSPENDED, WAIT_APPROVAL 등)
}
