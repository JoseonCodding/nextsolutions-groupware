package com.kdt.KDT_PJT.boards.di;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminOnlyInterceptor implements HandlerInterceptor {

    private static final String MASTER_ID = "20250004";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        String me = null;
        if (session != null && session.getAttribute("loginUser") != null) {
            Object u = session.getAttribute("loginUser");
            try { me = (String) u.getClass().getMethod("getEmployeeId").invoke(u); } catch (Exception ignore) {}
        }
        if (MASTER_ID.equals(me)) return true;
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
