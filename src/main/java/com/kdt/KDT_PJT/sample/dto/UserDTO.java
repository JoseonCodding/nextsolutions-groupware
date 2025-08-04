package com.kdt.KDT_PJT.sample.dto;

import java.time.LocalDate;

public class UserDTO {

    private String empNo;           // 사번
    private String name;            // 이름
    private String phone;           // 휴대폰 번호
    private LocalDate birthDate;    // 생년월일
    private LocalDate hireDate;     // 입사일
    private LocalDate retireDate;   // 퇴사일
    private String departmentName;  // 부서명
    private String position;        // 직위
    private String role;            // 권한
    private boolean passwordChanged; // 비밀번호 변경 여부

    public UserDTO() {
    }

    public UserDTO(String empNo, String name, String phone, LocalDate birthDate, LocalDate hireDate,
                   LocalDate retireDate, String departmentName, String position, String role, boolean passwordChanged) {
        this.empNo = empNo;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.retireDate = retireDate;
        this.departmentName = departmentName;
        this.position = position;
        this.role = role;
        this.passwordChanged = passwordChanged;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getRetireDate() {
        return retireDate;
    }

    public void setRetireDate(LocalDate retireDate) {
        this.retireDate = retireDate;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }
}