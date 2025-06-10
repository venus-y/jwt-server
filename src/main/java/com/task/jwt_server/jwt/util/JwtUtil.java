package com.task.jwt_server.jwt.util;

import com.task.jwt_server.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {
  private static final String BEARER_PREFIX = "Bearer ";

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Value("${spring.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  private Key key;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // Access Token 생성
  public String createAccessToken(User user) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole().name())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + accessTokenExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("토큰 만료: {}", e.getMessage());
    } catch (JwtException | IllegalArgumentException e) {
      log.error("유효하지 않은 토큰: {}", e.getMessage());
    }
    return false;
  }

  // Refresh Token 생성
//  public String createRefreshToken(User user) {
//    Date now = new Date();
//    return Jwts.builder()
//        .setSubject(user.getUserId().toString())
//        .setIssuedAt(now)
//        .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
//        .signWith(key, SignatureAlgorithm.HS256)
//        .compact();
//  }

  // Claims 추출
  public Claims extractClaims(String token) {
    return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token.replace(BEARER_PREFIX, ""))
        .getBody();
  }

  // Bearer 제거
  public String extractPrefix(String token) {
    return token.substring(7);
  }





}
