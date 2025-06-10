package com.task.jwt_server.presentation;

import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원 API", description = "일반 사용자 회원 가입 및 로그인 API입니다.")
public interface UserApi {

  @Operation(summary = "회원 가입", description = "일반 사용자를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원 가입 성공",
          content = @Content(schema = @Schema(implementation = CreateUserResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content)
  })
  ResponseEntity<?> signUp(
      @RequestBody(
          description = "회원가입 요청",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = """
                  {
                    "username": "user123",
                    "password": "userPassword1!",
                    "nickname": "usernick"
                  }
                  """)
          )
      )
      CreateUserRequestDto request
  );

  @Operation(summary = "로그인", description = "사용자 인증 후 토큰을 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = SignInUserResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  ResponseEntity<?> singIn(
      @RequestBody(
          description = "로그인 요청",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = """
                  {
                    "username": "user123",
                    "password": "userPassword1!"
                  }
                  """)
          )
      )
      SignInUserRequestDto request
  );
}
