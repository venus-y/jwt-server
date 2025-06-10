package com.task.jwt_server.presentation;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.jwt_server.application.dto.request.CreateUserRequestDto;
import com.task.jwt_server.application.dto.request.SignInUserRequestDto;
import com.task.jwt_server.infrastructure.repository.UserRepositoryImpl;
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
class UserControllerTest {

  @Autowired
  private WebApplicationContext context;

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
  @DisplayName("회원가입 후 로그인")
  void signIn() throws Exception {

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

    // given
    SignInUserRequestDto signInUserRequest = new SignInUserRequestDto("user123", "Password1!");
    String signInRequestJson = objectMapper.writeValueAsString(signInUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signInRequestJson)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());

  }

  @Test
  @DisplayName("회원가입 후 로그인 실패")
  void failedSignIn() throws Exception {
    // given
    CreateUserRequestDto createUserRequest = new CreateUserRequestDto("user123", "Password1!",
        "닉네임");
    String createRequestJson = objectMapper.writeValueAsString(createUserRequest);

    SignInUserRequestDto signInUserRequest = new SignInUserRequestDto("user123", "wrongValue");
    String signInRequestJson = objectMapper.writeValueAsString(signInUserRequest);

    // when & then
    mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user123"))
        .andExpect(jsonPath("$.nickname").value("닉네임"));

    mockMvc.perform(
            post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signInRequestJson)
        ).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
        .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));

  }

  @Test
  @DisplayName("중복 회원가입")
  void duplicateSignIn() throws Exception {

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

    mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson)
        ).andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.message").value("이미 가입된 사용자입니다."));
  }


}