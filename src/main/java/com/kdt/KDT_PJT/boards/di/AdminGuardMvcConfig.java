package com.kdt.KDT_PJT.boards.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// 옵션) 토글용: import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class AdminGuardMvcConfig implements WebMvcConfigurer {

    @Autowired AdminOnlyInterceptor adminOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminOnlyInterceptor)
                .addPathPatterns(
                    "/admin/**",
                    "/board/notice/write",
                    "/board/notice/save",
                    "/board/notice/approve",
                    "/board/notice/reject"
                )
                .excludePathPatterns(
                    "/board/notice",                // 목록/리다이렉트
                    "/board/notice/detail",         // 상세 열람
                    "/board/notice/like/**"         // 좋아요는 열람 권한에 붙이기
                );
    }
}
