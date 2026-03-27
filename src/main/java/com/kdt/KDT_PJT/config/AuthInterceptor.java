package com.kdt.KDT_PJT.config;

import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        if (uri.startsWith("/img/")) {
            return true;
        }

        EmployeeDto loginUser = (EmployeeDto) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // 현재 요청의 company_id를 Thread-Local에 저장
        CompanyContext.set(loginUser.getCompanyId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 요청 종료 후 반드시 정리 (메모리 누수 방지)
        CompanyContext.clear();
    }
}
