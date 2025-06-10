package com.task.jwt_server.application.exception;

import com.task.jwt_server.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends CustomException {

  public UserException(String errorCode, String message, HttpStatus status) {
    super(errorCode, message, status);
  }

  public static class DuplicateUserName extends UserException {

    public DuplicateUserName() {
      super("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다.", HttpStatus.CONFLICT);
    }
  }
}
