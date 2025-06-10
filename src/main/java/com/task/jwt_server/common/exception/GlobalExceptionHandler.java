package com.task.jwt_server.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<?> handleCustomException(CustomException e) {
    return ResponseEntity
        .status(e.getStatus())
        .body(Error.of(e.getErrorCode(), e.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(Error.of("ACCESS_DENIED", "접근 권한이 없습니다"));
  }

  @Getter
  public static class Error {

    private String errorCode;
    private String message;

    public Error(String errorCode, String message) {
      this.errorCode = errorCode;
      this.message = message;
    }

    public static Error of(String errorCode, String message) {
      return new Error(errorCode, message);
    }
  }
}
