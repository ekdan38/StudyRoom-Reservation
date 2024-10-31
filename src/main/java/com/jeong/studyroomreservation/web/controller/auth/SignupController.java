package com.jeong.studyroomreservation.web.controller.auth;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.Refresh;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.repository.RefreshRepository;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j(topic = "authController")
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
        UserDto requestUserDto = userMapper.SingnupRequestDtoToUserDto(requestDto);
        UserDto userDto = userService.signup(requestUserDto);
        ResponseDto<UserDto> responseBody = new ResponseDto<>("Success Signup", userDto);
        return ResponseEntity.ok().body(responseBody);
    }


}
