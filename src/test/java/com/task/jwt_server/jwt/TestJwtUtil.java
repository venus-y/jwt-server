package com.task.jwt_server.jwt;

import com.task.jwt_server.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestJwtUtil {

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

  // 일반적인 jwt 생성
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

  // 발행과 즉시 만료
  public String createShortExpiredTimeAccessToken(User user) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole().name())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime()))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 잘못된 포멧의 jwt 생성
  public String createInvalidFormatAccessToken(User user) {
    return "wrongValue";
  }
}