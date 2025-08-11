package com.kdt.KDT_PJT.boards.di;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Component
public class AdminOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        Object uObj = (session != null) ? session.getAttribute("loginUser") : null;

        if (uObj == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        EmployeeDto u = (EmployeeDto) uObj;

        // 너희 조직 기준에 맞게 판단: deptName/employeeId/role 등
        boolean isAdmin = "20250002".equals(u.getEmployeeId()) // 예) 슈퍼관리자
                        || "게시판관리부".equals(u.getDeptName());
        boolean isNoticeMgr = "공지사항부".equals(u.getDeptName());

        String path = req.getRequestURI();

        // /admin/** 는 관리자만
        if (path.startsWith(req.getContextPath() + "/admin/") && isAdmin) return true;

        // 공지 작성/저장/승인/반려는 공지담당자 또는 관리자만
        if (path.startsWith(req.getContextPath() + "/board/notice/")) {
            if (path.endsWith("/write") || path.endsWith("/save") || path.endsWith("/approve") || path.endsWith("/reject")) {
                if (isAdmin || isNoticeMgr) return true;
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true; // 나머지는 통과
    }
}
