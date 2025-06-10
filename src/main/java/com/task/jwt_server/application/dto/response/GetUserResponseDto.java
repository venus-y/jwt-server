package com.task.jwt_server.application.dto.response;

import com.task.jwt_server.domain.entity.User.UserRole;

public record GetUserResponseDto(String username, String nickname, UserRole role) {

}
