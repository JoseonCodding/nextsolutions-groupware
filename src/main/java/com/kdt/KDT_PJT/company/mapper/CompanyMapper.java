package com.kdt.KDT_PJT.company.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CompanyMapper {

    /** 신규 회사 INSERT */
    void insertCompany(@Param("companyNm") String companyNm,
                       @Param("ownerEmail") String ownerEmail);

    /** 방금 등록한 회사의 company_id 조회 (이메일 기준) */
    int selectCompanyIdByEmail(@Param("ownerEmail") String ownerEmail);

    /** 관리자 계정 INSERT */
    void insertAdminEmployee(@Param("employeeId") String employeeId,
                             @Param("password")   String password,
                             @Param("adminNm")    String adminNm,
                             @Param("phone")      String phone,
                             @Param("companyId")  int companyId);

    /** 이메일 중복 확인 */
    int countByOwnerEmail(@Param("ownerEmail") String ownerEmail);
}
