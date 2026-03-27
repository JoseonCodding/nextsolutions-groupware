package com.kdt.KDT_PJT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 * - URL 인증은 기존 AuthInterceptor(세션 기반)로 처리.
 * - 기존 HTML 폼이 th:action(CSRF 토큰 자동 포함)을 사용하지 않으므로 CSRF는 비활성화.
 *   추후 모든 폼을 th:action으로 전환 시 재활성화 예정.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
