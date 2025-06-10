package com.task.jwt_server.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record CreateUserRequestDto(@NotNull String username, @NotNull String password,@NotNull String nickname) {

}
