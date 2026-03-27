package com.kdt.KDT_PJT.company.dto;

import lombok.Data;

@Data
public class CompanyRegisterDto {

    // 회사 정보
    private String companyNm;   // 회사명
    private String ownerEmail;  // 대표 이메일

    // 관리자 계정 정보
    private String adminNm;     // 관리자 이름
    private String phone;       // 연락처
    private String password;    // 비밀번호
    private String passwordConfirm; // 비밀번호 확인
}
