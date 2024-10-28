package com.jeong.studyroomreservation.web.security;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.LoginIdNotFoundException;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws LoginIdNotFoundException {
        User user= userRepository.findByLoginId(loginId).orElseThrow(() -> new LoginIdNotFoundException(ErrorCode.LOGINID_NOTFOUND));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        UserDto userDto = userMapper.entityToSignupDto(user);
        return new JwtUserDetails(userDto, authorities);
    }
}
