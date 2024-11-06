package com.jeong.studyroomreservation.web.controller.auth;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @RequestMapping("/api/test")
    public ResponseEntity<?> test (@AuthenticationPrincipal UserDto userDto){
        Boolean authenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", authenticated);
        ResponseDto<Map<String, Object>> responseBody = new ResponseDto<>("Authorization Success", response);
        return ResponseEntity.ok().body(responseBody);
    }
}
