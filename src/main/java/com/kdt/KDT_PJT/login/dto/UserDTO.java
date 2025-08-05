package com.kdt.KDT_PJT.login.dto;

public class UserDTO {
    private String empNo;     // 사번
    private String name;      // 이름
    private String password;  // 비밀번호

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
