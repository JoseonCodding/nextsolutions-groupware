package com.kdt.KDT_PJT.api_p;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	
    	
        registry.addMapping("/api/**") // 모든 /api/** 엔드포인트에 대해서 CORS를 허용합니다.
                .allowedOrigins("http://localhost:3000") // 허용할 오리진을 설정합니다. React 앱의 주소에 맞게 변경해주세요.
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드를 설정합니다.
                .allowCredentials(true); // 인증 정보를 전송할 수 있도록 허용합니다.
    }
}