package com.kdt.KDT_PJT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager mgr = new InMemoryUserDetailsManager();
        mgr.createUser(User.withUsername("user")
                          .password("{noop}pass")
                          .roles("USER")
                          .build());
        mgr.createUser(User.withUsername("admin")
                          .password("{noop}pass")
                          .roles("ADMIN")
                          .build());
        mgr.createUser(User.withUsername("notice")
                          .password("{noop}pass")
                          .roles("NOTICE")
                          .build());
        return mgr;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(authz -> authz
             // 정적 자원 허용
             .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
             // 로그인·로그아웃 페이지도 기본 로그인 페이지 사용하므로 허용
             .requestMatchers("/logout").permitAll()
             // 자유게시판 목록·상세·페이지 접근 (모든 역할)
             .requestMatchers("/boards", "/boards/**")
               .hasAnyRole("USER","ADMIN","NOTICE")
             // 게시글 작성/저장 (USER, ADMIN)
             .requestMatchers("/boards/form", "/boards/save")
               .hasAnyRole("USER","ADMIN")
             // 게시판 관리 (ADMIN)
             .requestMatchers("/types/**")
               .hasRole("ADMIN")
             // 그 외 모든 요청은 인증된 사용자만
             .anyRequest().authenticated()
          )
          // 기본 로그인 페이지 및 프로세싱 URL 사용
          .formLogin(Customizer.withDefaults())
          .logout(logout -> logout
             .logoutUrl("/logout")
             .logoutSuccessUrl("/login?logout")
             .permitAll()
          );
        return http.build();
    }
}
