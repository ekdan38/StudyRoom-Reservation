package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "authController")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<ResponseDto<UserDto>> signup(@RequestBody @Validated SignupRequestDto requestDto){
        UserDto requestUserDto = userMapper.SingnupRequestDtoToUserDto(requestDto);
        UserDto userDto = userService.signup(requestUserDto);
        ResponseDto<UserDto> responseBody = new ResponseDto<>("Success Signup", userDto);
        return ResponseEntity.ok().body(responseBody);
    }
}
