package com.jeong.studyroomreservation.web.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.web.security.AuthenticationToken.RestAuthenticationToken;
import com.jeong.studyroomreservation.web.security.userdetails.CustomUserDetails;
import com.jeong.studyroomreservation.web.security.userdetails.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j(topic = "[JwtFilter]")
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            Map<String, String> errorMessage = new HashMap<>();
            errorMessage.put("message", "Access token expired");
            response.getWriter().write(objectMapper.writeValueAsString(errorMessage));

            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            Map<String, String> errorMessage = new HashMap<>();
            errorMessage.put("message", "Invalid access token");
            response.getWriter().write(objectMapper.writeValueAsString(errorMessage));

            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);

        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
        RestAuthenticationToken restAuthenticationToken = new RestAuthenticationToken(customUserDetails.getUserDto(), null, customUserDetails.getAuthorities());

        //로그인 유지
        SecurityContextHolder.getContext().setAuthentication(restAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
