package com.task.jwt_server.application.dto.response;

import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.entity.User.UserRole;
import lombok.Getter;

public record CreateUserResponseDto(String username, String nickname, UserRole userRole) {

  public static CreateUserResponseDto from(User user) {
    return new CreateUserResponseDto(user.getUsername(), user.getNickname(), user.getRole());
  }
}
