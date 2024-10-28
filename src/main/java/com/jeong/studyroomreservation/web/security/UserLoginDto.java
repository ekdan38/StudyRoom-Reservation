package com.jeong.studyroomreservation.web.security;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginDto {
    private String loginId;
    private String password;
}
