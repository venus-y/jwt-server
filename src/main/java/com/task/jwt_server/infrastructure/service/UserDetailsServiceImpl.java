package com.task.jwt_server.infrastructure.service;

import com.task.jwt_server.application.exception.AuthException;
import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.repository.UserRepository;
import com.task.jwt_server.infrastructure.userdetails.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // username을 통해 User를 DB에서 조회
    User user = userRepository.findByUsername(username)
        .orElseThrow(AuthException.NotFoundUser::new); // 사용자 찾을 수 없을 때 예외 던짐

    // User 객체를 UserDetailsImpl에 전달
    return new UserDetailsImpl(user); // User 객체를 전달하여 UserDetailsImpl 생성
  }
}
