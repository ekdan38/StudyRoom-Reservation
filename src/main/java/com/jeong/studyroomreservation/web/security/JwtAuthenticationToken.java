package com.jeong.studyroomreservation.web.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    // 인증 후에는 dto객체를 넣어줄거임. 그래서 Object 로 선언
    private final Object principal;
    private final Object credentials;


    // manager 가기 전 토큰
    public JwtAuthenticationToken(Object principal, Object credentials) {
        super(null);
        setAuthenticated(false);
        this.principal = principal;
        this.credentials = credentials;
    }

    // provider 후에 인증 완료된 토큰
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        setAuthenticated(true);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
