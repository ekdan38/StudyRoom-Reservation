package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.PasswordNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider  implements AuthenticationProvider {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        JwtUserDetails userDetails = (JwtUserDetails) jwtUserDetailsService.loadUserByUsername(loginId);
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            log.info("provider 오류");

            throw new PasswordNotFoundException(ErrorCode.PASSWORD_NOTFOUND);
        }

        log.info("provider 정상 수행");
        return new JwtAuthenticationToken(userDetails.getAuthorities(), userDetails.getUserDto(), null);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }
}
