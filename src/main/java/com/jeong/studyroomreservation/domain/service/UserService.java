package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.EmailAlreadyExistsException;
import com.jeong.studyroomreservation.domain.error.exception.PhoneNumberAlreadyExistsException;
import com.jeong.studyroomreservation.domain.error.exception.UsernameAlreadyExistsException;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "[UserService]")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto signup(UserDto requestDto){
        // 비밀번호 암호화
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        // loginId 중복 조회
        String username = requestDto.getUsername();
        if(userRepository.existsByUsername(username)){
            log.error("Exists username = {}", username);
            throw new UsernameAlreadyExistsException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        String email = requestDto.getEmail();
        if(userRepository.existsByEmail(email)){
            log.error("Exists Email = {}", email);
            throw new EmailAlreadyExistsException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String phoneNumber = requestDto.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            log.error("Exists phoneNumber = {}", username);
            throw new PhoneNumberAlreadyExistsException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);

        }

        // User 저장
        User user = User.createUser(requestDto);
        return userMapper.userToUserDto(userRepository.save(user));
    }

}
