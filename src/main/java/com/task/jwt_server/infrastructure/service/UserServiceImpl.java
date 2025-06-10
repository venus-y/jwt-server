package com.task.jwt_server.infrastructure.service;

import com.task.jwt_server.application.UserService;
import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.GetUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;
import com.task.jwt_server.application.exception.AuthException;
import com.task.jwt_server.application.exception.AuthException.NotFoundUser;
import com.task.jwt_server.application.exception.UserException;
import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.repository.UserRepository;
import com.task.jwt_server.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public CreateUserResponseDto createUser(CreateUserRequestDto request) {
    if (existsByUsername(request.username())) {
      throw new UserException.DuplicateUserName();
    }
    Long nextUserId = userRepository.findNextUserId();
    User user = User.createUser(nextUserId, request.username(),
        passwordEncoder.encode(request.password()),
        request.nickname());
    User savedUser = userRepository.save(user);
    return CreateUserResponseDto.from(savedUser);
  }

  @Override
  public GetUserResponseDto findUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(NotFoundUser::new);
    return new GetUserResponseDto(user.getUsername(), user.getNickname(), user.getRole());
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public SignInUserResponseDto signIn(SignInUserRequestDto request) {
    User user = userRepository.findByUsername(request.username())
        .orElseThrow(AuthException.NotFoundUser::new);
    if (isInvalidCredential(request, user)) {
      throw new AuthException.InvalidCredential();
    }
    String accessToken = jwtUtil.createAccessToken(user);
    return new SignInUserResponseDto(accessToken);
  }

  @Override
  public CreateUserResponseDto createAdmin(CreateUserRequestDto request) {
    if (existsByUsername(request.username())) {
      throw new UserException.DuplicateUserName();
    }
    Long nextUserId = userRepository.findNextUserId();

    User user = User.createAdmin(nextUserId, request.username(),
        passwordEncoder.encode(request.password()),
        request.nickname());
    User savedUser = userRepository.save(user);
    return CreateUserResponseDto.from(savedUser);
  }

  private boolean isInvalidCredential(SignInUserRequestDto request, User user) {
    return !passwordEncoder.matches(request.password(), user.getPassword());
  }
}
