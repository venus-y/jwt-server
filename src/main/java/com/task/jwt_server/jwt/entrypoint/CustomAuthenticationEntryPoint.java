package com.task.jwt_server.jwt.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
    response.setContentType("application/json;charset=UTF-8");
    String errorMessage = createErrorMessage();
    response.getWriter().write(errorMessage);
  }

  private String createErrorMessage() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> error = Map.of(
        "error", Map.of(
            "code", "INVALID_TOKEN",
            "message", "유효하지 않은 인증 토큰입니다."
        )
    );

    return mapper.writeValueAsString(error);
  }

}
