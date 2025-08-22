package com.kdt.KDT_PJT.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	System.out.println("들어오냐?"+request.getRequestURI());
    	String uri = request.getRequestURI();
    	if(uri.startsWith("/img/")) {
    		return  true;
    	}
    	
    	
        Object loginUser = request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
