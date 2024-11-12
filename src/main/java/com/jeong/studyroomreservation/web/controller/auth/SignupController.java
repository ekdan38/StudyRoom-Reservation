package com.jeong.studyroomreservation.web.controller.auth;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j(topic = "SignupController")
@RequiredArgsConstructor
public class SignupController {

    private final UserMapper userMapper;
    private final UserService userService;


    @PostMapping("/api/signup")
    public ResponseEntity<ResponseDto<?>> signup(@RequestBody @Validated SignupRequestDto requestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }
        UserDto requestUserDto = userMapper.requestDtoToUserDto(requestDto);
        UserDto userDto = userService.signup(requestUserDto);
        ResponseDto<UserDto> responseBody = new ResponseDto<>("Signup Success", userDto);
        return ResponseEntity.ok().body(responseBody);
    }


}
