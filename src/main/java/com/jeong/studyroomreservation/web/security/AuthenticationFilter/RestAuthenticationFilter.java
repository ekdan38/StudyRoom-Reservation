package com.jeong.studyroomreservation.web.security.AuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.web.security.AuthenticationToken.RestAuthenticationToken;
import com.jeong.studyroomreservation.web.security.dto.LoginDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public RestAuthenticationFilter() {
        // "/api/login" 으로 Post 요청만 해당 필터가 처리 즉, 로그인 처리 수행 조건.
        super(new AntPathRequestMatcher("/api/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //굳이 ajax 요청인지 확인하지 않음.(restApi이기때문에 불필요하다고 판단)
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getReader(), LoginDto.class);

        RestAuthenticationToken restAuthenticationToken =
                new RestAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        return getAuthenticationManager().authenticate(restAuthenticationToken);
    }
}
