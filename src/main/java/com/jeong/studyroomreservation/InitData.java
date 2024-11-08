package com.jeong.studyroomreservation;

import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.pendingcompany.PendingCompanyRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

//@Component
@RequiredArgsConstructor
public class InitData {
    private static int USER_CNT = 0;
    private static int COMPANY_CNT = 0;

    private final PendingCompanyService pendingCompanyService;
    private final UserService userService;
    private final PendingCompanyMapper pendingCompanyMapper;
    @PostConstruct
    public void init(){
        UserDto userDto1 = createUserDto(UserRole.ROLE_SYSTEM_ADMIN);
        userService.signup(userDto1);
        for(int i = 0 ; i < 10; i ++){
            UserDto signupDto = userService.signup(createUserDto(UserRole.ROLE_USER));
            pendingCompanyService.createAndSave(createPendingDto(signupDto));
        }

    }
    private UserDto createUserDto(UserRole userRole){
        ++USER_CNT;
        return new UserDto(
                "testUsername" + USER_CNT,
                "testpassword@",
                "test" + USER_CNT,
                "test" + USER_CNT + "@gmail.com",
                "010-0000-" + (1000 + USER_CNT),
                userRole);
    }

    private PendingCompanyDto createPendingDto(UserDto userDto){
        ++COMPANY_CNT;;
        PendingCompanyRequestDto pendingCompanyRequestDto = new PendingCompanyRequestDto("Company" + COMPANY_CNT,
                "Description" + COMPANY_CNT,
                "Location" + COMPANY_CNT,
                "010-0000-" + (1000 + USER_CNT));
        return pendingCompanyMapper.requestToDto(pendingCompanyRequestDto, userDto.getId());
    }

}
