package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class JwtUserDetails implements UserDetails {

    private final UserDto userDto;
    private final List<GrantedAuthority> authorities;

    public JwtUserDetails(UserDto userDto, List<GrantedAuthority> authorities) {
        this.userDto = userDto;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return userDto.getLoginId();
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }
}
