package com.task.jwt_server.infrastructure.userdetails;

import com.task.jwt_server.domain.entity.User;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsImpl implements UserDetails {

  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  @Getter
  private final User user;

  // 생성자
  public UserDetailsImpl(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.authorities = user.getAuthorities();
    this.user = user;
  }

  public static UserDetailsImpl fromUser(User user) {
    return new UserDetailsImpl(user);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }


  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @Override
  public boolean isEnabled() {
    return true;
  }
}
