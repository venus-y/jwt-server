package com.task.jwt_server.jwt.filter;

import com.task.jwt_server.application.exception.AuthException.NotFoundUser;
import com.task.jwt_server.application.exception.JwtException;
import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.repository.UserRepository;
import com.task.jwt_server.infrastructure.userdetails.UserDetailsImpl;
import com.task.jwt_server.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

  private static final String[] EXCLUDED_PATHS = {
      "/api/users/signup", "/api/users/signin",
      "/api/admin/signup", "/api/admin/signin",
      "/swagger-ui",
      "/.well-known",
      "/v3/api-docs",
      "/swagger-resources/",
      "/webjars/"
  };

  private static final String JWT_HEADER_NAME = "Authorization";
  private static final String JWT_PREFIX = "Bearer ";
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  private boolean validatePath(String path) {
    for (String excludePath : EXCLUDED_PATHS) {
      if (path.startsWith(excludePath)) {
        return true;
      }
    }
    return false;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = request.getHeader(JWT_HEADER_NAME);

    if (token == null) {
      throw new JwtException("토큰이 존재하지 않습니다");
    }

    if (validateJwtPrefix(token)) {
      String extractedToken = jwtUtil.extractPrefix(token);
      validateJwtToken(extractedToken);

      Claims claims = jwtUtil.extractClaims(extractedToken);
      String username = claims.getSubject();
      User user = userRepository.findByUsername(username)
          .orElseThrow(NotFoundUser::new);
      UserDetailsImpl userDetails = UserDetailsImpl.fromUser(user);
      Authentication authentication = createAuthentication(userDetails);

      SecurityContext securityContext = SecurityContextHolder.getContext();
      securityContext.setAuthentication(authentication);
      filterChain.doFilter(request, response);
      SecurityContextHolder.clearContext();
    }

  }

  private UsernamePasswordAuthenticationToken createAuthentication(
      UserDetailsImpl userDetails) {
    return new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
  }

  private boolean validateJwtPrefix(String token) {
    return token.startsWith(JWT_PREFIX);
  }

  private void validateJwtToken(String extractedToken) {
    if (!jwtUtil.validateToken(extractedToken)) {
      throw new JwtException("유효하지 않은 인증 토큰입니다.");
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return validatePath(path);
  }
}
