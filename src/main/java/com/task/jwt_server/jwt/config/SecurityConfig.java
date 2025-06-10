package com.task.jwt_server.jwt.config;

import com.task.jwt_server.jwt.filter.JwtSecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final JwtSecurityFilter jwtSecurityFilter;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))  // Stateless 세션 관리
        .authorizeHttpRequests(auth -> auth
            // 공개 API 경로: 누구나 접근 가능
            // Swagger UI 및 API 문서 경로를 공개
            .requestMatchers("/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",// Swagger 보안 설정
                "/api/users/signup",//
                "/api/users/signin",
                "/api/admin/signup",
                "/api/admin/signin"// 일반적인 정적 파일 위치)
            )
            .permitAll()

            // 나머지 요청은 인증이 필요
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(jwtSecurityFilter,
            UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
        .build();
  }

}
