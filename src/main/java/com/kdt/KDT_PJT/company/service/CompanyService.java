package com.kdt.KDT_PJT.company.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.KDT_PJT.company.dto.CompanyRegisterDto;
import com.kdt.KDT_PJT.company.mapper.CompanyMapper;

@Service
public class CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean isEmailDuplicate(String ownerEmail) {
        return companyMapper.countByOwnerEmail(ownerEmail) > 0;
    }

    /**
     * 회사 등록 + 관리자 계정 생성 (트랜잭션)
     *
     * @return 생성된 관리자 사번 (로그인 ID)
     */
    @Transactional
    public String register(CompanyRegisterDto dto) {
        // 1. 회사 INSERT
        companyMapper.insertCompany(dto.getCompanyNm(), dto.getOwnerEmail());

        // 2. 방금 생성된 company_id 조회
        int companyId = companyMapper.selectCompanyIdByEmail(dto.getOwnerEmail());

        // 3. 관리자 사번 생성: companyId 4자리 + "0001"
        String employeeId = String.format("%04d0001", companyId);

        // 4. 비밀번호 BCrypt 해싱
        String hashedPw = encoder.encode(dto.getPassword());

        // 5. 관리자 직원 INSERT
        companyMapper.insertAdminEmployee(employeeId, hashedPw, dto.getAdminNm(), dto.getPhone(), companyId);

        return employeeId;
    }
}
