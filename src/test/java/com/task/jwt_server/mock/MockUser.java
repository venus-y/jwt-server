package com.task.jwt_server.mock;

import com.task.jwt_server.domain.entity.User.UserRole;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithSecurityContextFactoryImpl.class)
public @interface MockUser {

  long id() default 1;

  String username() default "mockUser";

  String password() default "mockPassword";

  String nickname() default "mockNickname";

  UserRole role() default UserRole.ROLE_ADMIN;

}
