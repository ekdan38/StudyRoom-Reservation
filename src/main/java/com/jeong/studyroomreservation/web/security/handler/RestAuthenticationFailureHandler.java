package com.jeong.studyroomreservation.web.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j(topic = "[RestAuthenticationFailureHandler]")
@RequiredArgsConstructor
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //인증 실패 로직
        log.error("AuthenticationFailed = {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("errorMessage", exception.getMessage());

        ResponseDto<Map<String, Object>> responseBody =
                new ResponseDto<>("Authentication Failed", data);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));

    }
}
