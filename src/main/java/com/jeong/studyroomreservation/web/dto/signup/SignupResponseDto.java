package com.jeong.studyroomreservation.web.dto.signup;

import com.jeong.studyroomreservation.domain.entity.UserRole;
import lombok.Data;

@Data
public class SignupResponseDto {

    private Long id;
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole userRole;

}
