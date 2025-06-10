package com.task.jwt_server.domain.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class User {

  private Long userId;
  private String username;
  private String password;
  private String nickname;
  private UserRole role;


  @Builder(access = AccessLevel.PRIVATE)
  private User(Long userId, String username, String password, String nickname, UserRole role) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.role = role;
  }

  public static User createUser(Long userId, String username, String password, String nickname) {
    return User.builder()
        .userId(userId)
        .username(username)
        .password(password)
        .nickname(nickname)
        .role(UserRole.ROLE_USER)
        .build();
  }

  public static User createAdmin(Long userId, String username, String password, String nickname) {
    return User.builder()
        .userId(userId)
        .username(username)
        .password(password)
        .nickname(nickname)
        .role(UserRole.ROLE_ADMIN)
        .build();
  }

  public Collection<SimpleGrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(this.role.name()));
    return authorities;
  }

  public enum UserRole {
    ROLE_ADMIN, ROLE_USER
  }
}
