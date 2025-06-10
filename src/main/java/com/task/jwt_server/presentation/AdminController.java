package com.task.jwt_server.presentation;

import com.task.jwt_server.application.UserService;
import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.GetUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController implements AdminAPi {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(@RequestBody @Valid CreateUserRequestDto request) {
    CreateUserResponseDto response = userService.createAdmin(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody @Valid SignInUserRequestDto request) {
    SignInUserResponseDto response = userService.signIn(request);
    return ResponseEntity.ok(response);
  }

  @Secured("ROLE_ADMIN")
  @PatchMapping("/users/{userId}/roles")
  public ResponseEntity<?> grantAdminRoles(@PathVariable Long userId) {
    GetUserResponseDto response = userService.findUser(userId);
    return ResponseEntity.ok(response);
  }
}
