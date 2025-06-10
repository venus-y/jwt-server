package com.task.jwt_server.presentation;

import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.dto.response.CreateUserResponseDto;
import com.task.jwt_server.application.dto.response.GetUserResponseDto;
import com.task.jwt_server.application.dto.response.SignInUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "관리자 API", description = "관리자 회원 가입, 로그인 및 권한 관리 API입니다.")
public interface AdminAPi {

  @Operation(summary = "관리자 회원가입", description = "관리자 계정을 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "관리자 생성 성공",
          content = @Content(schema = @Schema(implementation = CreateUserResponseDto.class)))
  })
  ResponseEntity<?> signUp(
      @RequestBody(
          description = "관리자 회원가입 요청",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = """
                  {
                    "username": "admin1",
                    "password": "admin123!",
                    "nickname": "admin1"
                  }
                  """)
          )
      )
      CreateUserRequestDto request
  );

  @Operation(summary = "관리자 로그인", description = "관리자 인증 후 JWT 토큰을 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = SignInUserResponseDto.class)))
  })
  ResponseEntity<?> signIn(
      @RequestBody(
          description = "관리자 로그인 요청",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = """
                  {
                    "username": "admin1",
                    "password": "admin123!"
                  }
                  """)
          )
      )
      SignInUserRequestDto request
  );

  @Operation(summary = "관리자 권한 부여", description = "특정 사용자에게 관리자 권한을 부여합니다. (ADMIN 권한 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "권한 부여 성공",
          content = @Content(schema = @Schema(implementation = GetUserResponseDto.class)))
  })
  ResponseEntity<?> grantAdminRoles(
      @Parameter(description = "관리자 권한을 부여할 사용자 ID", example = "1")
      Long userId
  );
}
