package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("일단 로그인 성공 여기성 이제 jwt 토큰 발급 해주면 됩니다요.");
        log.info(authentication.getPrincipal().toString());
        UserDto principal = (UserDto)authentication.getPrincipal();
        principal.setPassword(null);
        log.info(authentication.getPrincipal().toString());
    }
}
