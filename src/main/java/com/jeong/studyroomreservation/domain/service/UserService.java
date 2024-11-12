package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.*;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new SignupException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        String email = requestDto.getEmail();
        if(userRepository.existsByEmail(email)){
            log.error("Exists Email = {}", email);
            throw new SignupException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String phoneNumber = requestDto.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            log.error("Exists phoneNumber = {}", username);
            throw new SignupException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);

        }

        // User 저장
        User user = User.createUser(requestDto);
        return userMapper.entityToUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateRole(Long userId, UserRole updateUserRole){
        User user = findById(userId);
        user.updateUserRole(updateUserRole);
        return userMapper.entityToUserDto(user);
    }

    // 다른 service 로직에서 User 필요할 때 조회.
    // 사용 : PendingService
    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

}
