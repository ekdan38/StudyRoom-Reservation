package com.jeong.studyroomreservation.domain;

import com.jeong.studyroomreservation.domain.entity.UserRole;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import org.springframework.stereotype.Component;

@Component
public class RoleAssigner {

    public UserRole checkUserRole(SignupRequestDto requestDto){
        /**
         * 일단 권한은 클라이언트 전송으로만 해결한다.
         */
        UserRole role = UserRole.ROLE_USER;

        if(requestDto.isManager()){
            role = UserRole.ROLE_MANAGER;
        }
        if(requestDto.isAdmin()){
            role = UserRole.ROLE_ADMIN;
        }
        return role;
    }
}
