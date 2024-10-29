package com.jeong.studyroomreservation.web.security.AuthenticationToken;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RestAuthenticationToken extends AbstractAuthenticationToken {

    //인증 후에 UserDetails가 아니라 UserDto를 넣기위함.
    private final Object principal;
    private final Object credentials;

    //인증 과정 완료 후 사용할 토큰
    public RestAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
        this.principal = principal;
        this.credentials = credentials;
    }

    // filter 에서 세팅해줄 토큰
    public RestAuthenticationToken(Object principal, Object credentials) {
        super(null);
        setAuthenticated(false);
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
