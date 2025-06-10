package com.task.jwt_server.application.exception;

import com.task.jwt_server.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AuthException extends CustomException {

  public AuthException(String errorCode, String message, HttpStatus status) {
    super(errorCode, message, status);
  }

  public static class NotFoundUser extends AuthException {

    public NotFoundUser() {
      super("USER_NOT_FOUND", "회원정보가 존재하지 않습니다", HttpStatus.BAD_REQUEST);
    }
  }

  public static class InvalidCredential extends AuthException {

    public InvalidCredential() {
      super("INVALID_CREDENTIALS", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

  }
}
