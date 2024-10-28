package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JwtAuthorizationFilter")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthorizationFilter(JwtProvider jwtProvider, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtProvider = jwtProvider;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtProvider.getTokenFromRequest(request);
        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            tokenValue = jwtProvider.substringToken(tokenValue);
            log.info("tokenValue = {}", tokenValue);

            if (!jwtProvider.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }

            Claims info = jwtProvider.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error("Authentication Setup Failed: {}", e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String loginId) {
        log.info("로그인 유지 로직 실행");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(loginId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        //나중에 지울코드
        if(authentication == null || !authentication.isAuthenticated()){
            log.info("인증 객체 없다.....");
        }
        Authentication checkAuthentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        System.out.println("checkAuthentication.isAuthenticated() = " + checkAuthentication.isAuthenticated());
        UserDto principal = (UserDto)checkAuthentication.getPrincipal();
        System.out.println("principal = " + principal.toString());
        //여기까지 지울 코드

    }

    // 인증 객체 생성
    private Authentication createAuthentication(String loginId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) jwtUserDetailsService.loadUserByUsername(loginId);
        return new JwtAuthenticationToken(jwtUserDetails.getAuthorities(), jwtUserDetails.getUserDto(), null);
    }

}
