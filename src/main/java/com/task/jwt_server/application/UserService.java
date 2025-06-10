package com.task.jwt_server.application;

import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.GetUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;

public interface UserService {

  CreateUserResponseDto createUser(CreateUserRequestDto request);

  GetUserResponseDto findUser(Long userId);

  boolean existsByUsername(String username);

  SignInUserResponseDto signIn(SignInUserRequestDto request);

  CreateUserResponseDto createAdmin(CreateUserRequestDto request);

}
