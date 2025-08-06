package com.kdt.KDT_PJT.login.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String empNo;
    private String empName;
    private String password;
    private String status; // 활성/비활성
}