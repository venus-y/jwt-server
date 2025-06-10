package com.task.jwt_server.mock;

import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.entity.User.UserRole;
import com.task.jwt_server.infrastructure.userdetails.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithSecurityContextFactoryImpl implements WithSecurityContextFactory<MockUser> {

  @Override
  public SecurityContext createSecurityContext(MockUser mockUser) {
    User user;

    // ✅ 역할에 따라 분기
    if (mockUser.role().equals(UserRole.ROLE_ADMIN)) {
      user = User.createAdmin(mockUser.id(), mockUser.username(), mockUser.password(),
          mockUser.nickname());
    } else {
      user = User.createUser(mockUser.id(), mockUser.username(), mockUser.password(),
          mockUser.nickname());
    }

    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
            userDetails.getAuthorities());
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(usernamePasswordAuthenticationToken);
    return context;
  }
}
