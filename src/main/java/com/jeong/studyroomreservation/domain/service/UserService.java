package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.EmailAlreadyExistsException;
import com.jeong.studyroomreservation.domain.error.exception.LoginIdAlreadyExistsException;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "userService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto signup(UserDto requestDto){
        // 비밀번호 암호화
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        // loginId 중복 조회
        String loginId = requestDto.getLoginId();

        if(userRepository.existsByLoginId(loginId)){
            log.error("Exist LoginId = {}", loginId);
            throw new LoginIdAlreadyExistsException(ErrorCode.LOGINID_ALREADY_EXISTS);
        }

        String email = requestDto.getEmail();
        if(userRepository.existsByEmail(email)){
            log.error("Exist Email = {}", email);
//            throw new EmailAlreadyExistsException("Exist Email" + email);
            throw new EmailAlreadyExistsException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // User 저장
        User user = User.createUser(requestDto);
        return userMapper.entityToSignupDto(userRepository.save(user));
    }

}
