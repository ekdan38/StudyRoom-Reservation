package com.jeong.studyroomreservation.web.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j(topic = "[RestAuthenticationDeniedHandler]")
@RequiredArgsConstructor
public class RestAuthenticationDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // AccessDenied 처리
        log.error("AccessDenied = {}", accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errorMessage", "No authority");
        data.put("path", request.getRequestURI());

        ResponseDto<Map<String, Object>> responseBody =
                new ResponseDto<>("Access denied", data);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));

    }
}
