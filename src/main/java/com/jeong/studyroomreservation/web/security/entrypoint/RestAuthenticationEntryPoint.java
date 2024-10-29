package com.jeong.studyroomreservation.web.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "[RestAuthenticationEntryPoint]")
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("EntryPoint = {}", authException.getMessage());

        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("message", "Access denied: authentication required");
        errorMessage.put("errorMessage", authException.getMessage());
        errorMessage.put("path", request.getRequestURI());

        String responseBody = objectMapper.writeValueAsString(errorMessage);
        response.getWriter().write(responseBody);

    }
}
