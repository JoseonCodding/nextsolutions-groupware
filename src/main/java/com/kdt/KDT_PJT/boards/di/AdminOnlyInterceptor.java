package com.kdt.KDT_PJT.boards.di;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loginUser") instanceof EmployeeDto user) {
            if ("대표".equals(user.getRole())) return true;
        }
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
