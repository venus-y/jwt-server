package com.task.jwt_server.presentation;

import com.task.jwt_server.application.UserService;
import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(@RequestBody @Valid CreateUserRequestDto request) {
    CreateUserResponseDto response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> singIn(@RequestBody @Valid SignInUserRequestDto request) {
    SignInUserResponseDto response = userService.signIn(request);
    return ResponseEntity.ok(response);
  }
}
