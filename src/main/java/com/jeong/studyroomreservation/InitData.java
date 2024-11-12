package com.jeong.studyroomreservation;

import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.domain.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class InitData {
    private final UserService userService;
    private final PendingCompanyService pendingCompanyService;
    private final UserRepository userRepository;



    @PostConstruct
    public void setUp(){
        //SystemAdmin
        UserDto userDto = new UserDto(
                "systemAdmin",
                "systemAdmin@",
                "systemAdmin",
                "admin@gmail.com",
                "010-0000-1111",
                UserRole.ROLE_SYSTEM_ADMIN);
        userService.signup(userDto);
        //User
        createUser();

    }

    private void createUser(){
        for(int i = 0; i < 10; i++){
            UserDto userDto = new UserDto(
                    "Testusername" + (i + 1),
                    "testpassword@",
                    "testUser" + (i + 1),
                    "test" + (i + 1) + "@gmail.com",
                    "010-0000-" + (1000 + i + 1),
                    UserRole.ROLE_USER);
            UserDto signup = userService.signup(userDto);
            Long id = signup.getId();
            LocalTime localTime = LocalTime.of(9, 0);
            PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(
                    id,
                    "Company" + (i + 1),
                    "description",
                    "location",
                    "010-0000-" + (1000 + i + 1),
                    LocalTime.of(9,0),
                    LocalTime.of(18,0)
            );
            pendingCompanyService.createAndSave(pendingCompanyDto);
        }
    }
}
