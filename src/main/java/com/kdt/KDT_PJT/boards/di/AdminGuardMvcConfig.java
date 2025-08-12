package com.kdt.KDT_PJT.boards.di;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// 옵션) 토글용: import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
// @ConditionalOnProperty(name="board.admin-guard.enabled", havingValue="true", matchIfMissing=true)
public class AdminGuardMvcConfig implements WebMvcConfigurer {

    private final AdminOnlyInterceptor adminOnlyInterceptor;

    public AdminGuardMvcConfig(AdminOnlyInterceptor adminOnlyInterceptor) {
        this.adminOnlyInterceptor = adminOnlyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminOnlyInterceptor)
                .order(0) // 가장 먼저 실행
                .addPathPatterns("/admin/**", "/api/admin/**");
    }
}
