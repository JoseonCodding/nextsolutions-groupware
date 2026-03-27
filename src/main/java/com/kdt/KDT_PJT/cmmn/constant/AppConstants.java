package com.kdt.KDT_PJT.cmmn.constant;

import java.util.Set;

/**
 * 애플리케이션 전역 상수.
 * TODO: 추후 DB 기반 권한 테이블로 마이그레이션 예정
 */
public final class AppConstants {

    private AppConstants() {}

    // 모든 권한 체크는 EmployeeDto.role 필드 기반으로 처리
    // 사원관리: role == '사원' or '대표'
    // 게시판 관리: role == '게시판' or '대표'
    // 공지사항: role == '공지사항' or '대표'
    // 문서관리: role == '문서' or '대표'
}
