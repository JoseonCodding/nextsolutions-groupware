package com.kdt.KDT_PJT.cmmn.context;

/**
 * 현재 요청의 company_id를 Thread-Local에 저장.
 * AuthInterceptor가 요청 시작 시 set(), 종료 시 clear().
 */
public class CompanyContext {

    private static final ThreadLocal<Integer> COMPANY_ID = new ThreadLocal<>();

    public static void set(Integer companyId) {
        COMPANY_ID.set(companyId);
    }

    public static Integer get() {
        return COMPANY_ID.get();
    }

    public static void clear() {
        COMPANY_ID.remove();
    }
}
