package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.User;
import com.jeong.studyroomreservation.domain.entity.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JwtAuthenticationSuccessHandler")
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("JwtAuthenticationSuccessHandler");
        UserDto userDto = (UserDto)authentication.getPrincipal();
        userDto.setPassword(null);

        log.info("userDto.getLoginId() = {}", userDto.getLoginId());
        log.info("userDto.getRole() = {}", userDto.getRole());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContext context = SecurityContextHolder.getContextHolderStrategy().getContext();
        UserDto principal = (UserDto)context.getAuthentication().getPrincipal();
        log.info(principal.getClass().toString());
        log.info(principal.getName());
        String token = jwtProvider.createToken(userDto.getLoginId(), userDto.getRole());
        jwtProvider.addJwtToCookie(token, response);
    }
}
