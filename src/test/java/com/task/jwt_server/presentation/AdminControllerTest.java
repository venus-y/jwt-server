package com.task.jwt_server.presentation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.application.exception.JwtException;
import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.infrastructure.repository.UserRepositoryImpl;
import com.task.jwt_server.jwt.TestJwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AdminControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private TestJwtUtil jwtUtil;

  private MockMvc mockMvc;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @AfterEach
  void clear() {
    userRepository.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("관리자 회원가입 후 로그인")
  void signIn() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    // given
    SignInUserRequestDto signInUserRequest = new SignInUserRequestDto("admin123", "Password1!");
    String signInRequestJson = objectMapper.writeValueAsString(signInUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signInRequestJson)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());

  }

  @Test
  @DisplayName("관리자 회원가입 후 로그인 실패")
  void failedSignIn() throws Exception {
    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    SignInUserRequestDto signInUserRequest = new SignInUserRequestDto("admin123", "wrongValue");
    String signInRequestJson = objectMapper.writeValueAsString(signInUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    mockMvc.perform(
            post("/api/admin/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signInRequestJson)
        ).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
        .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));

  }

  @Test
  @DisplayName("관리자 중복 회원가입")
  void duplicateSignIn() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.message").value("이미 가입된 사용자입니다."));
  }

  @Test
  @DisplayName("관리자 권한 요청 - 관리자 회원")
  void grantAdminRoleRequestForAdmin() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    User user = userRepository.findByUsername(createUserRequest.username()).get();

    String accessToken = jwtUtil.createAccessToken(user);

    mockMvc.perform(
            patch("/api/admin/users/{userId}/roles", user.getUserId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("관리자 권한 요청 - 만료된 토큰")
  void grantAdminRoleRequestForAdminWithExpiredJWT() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    SignInUserRequestDto signInUserRequest = new SignInUserRequestDto("admin123", "Password1!");

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    User user = userRepository.findByUsername(createUserRequest.username()).get();

    String accessToken = jwtUtil.createShortExpiredTimeAccessToken(user);

    assertThatThrownBy(() ->
        mockMvc.perform(
            patch("/api/admin/users/{userId}/roles", user.getUserId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
    ).isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("관리자 권한 요청 - 잘못된 형식의 토큰")
  void grantAdminRoleRequestForAdminWithInvalidFormatJWT() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    User user = userRepository.findByUsername(createUserRequest.username()).get();

    String accessToken = jwtUtil.createInvalidFormatAccessToken(user);

    assertThatThrownBy(() ->
        mockMvc.perform(
            patch("/api/admin/users/{userId}/roles", user.getUserId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
        )
    ).isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("관리자 권한 요청 - 토큰 없이 요청")
  void grantAdminRoleRequestForAdminWithOutJWT() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("admin123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("admin123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    User user = userRepository.findByUsername(createUserRequest.username()).get();

    assertThatThrownBy(() ->
        mockMvc.perform(
            patch("/api/admin/users/{userId}/roles", user.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
        )
    ).isInstanceOf(JwtException.class);
  }


  @Test
  @DisplayName("관리자 권한 요청 - 일반 회원")
  void grantAdminRoleRequestForUser() throws Exception {

    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("user123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    User user = userRepository.findByUsername(createUserRequest.username()).get();

    String accessToken = jwtUtil.createAccessToken(user);

    mockMvc.perform(
            patch("/api/admin/users/{userId}/roles", user.getUserId()) // ✅ PathVariable 주입
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
        ).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.message").value("접근 권한이 없습니다"));
  }


}