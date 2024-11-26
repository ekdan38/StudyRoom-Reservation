package com.jeong.studyroomreservation;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Component
@RequiredArgsConstructor
public class InitData {
    private final UserService userService;
    private final PendingCompanyService pendingCompanyService;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final StudyRoomService studyRoomService;
    private final ReservationService reservationService;

//    @PostConstruct
    public void createStudyRoom(){
        UserDto userDto = new UserDto(
                "Testusername111",
                "testpassword@",
                "testUser99",
                "test111@gmail.com",
                "010-1111-9999",
                UserRole.ROLE_STUDYROOM_ADMIN);
        UserDto signup = userService.signup(userDto);
        Long userId = signup.getId();
        User user = userRepository.findById(userId).get();

        CompanyDto companyDto = new CompanyDto("TestCompany111", "descripton", "lcoation",
                "010-0001-0000", LocalTime.of(9, 0), LocalTime.of(22, 0));
        Company company = Company.createCompany(companyDto, user);
        Company savedCompany = companyRepository.save(company);

        List<MultipartFile> file = new ArrayList<>();
        StudyRoomDto studyRoomDto = new StudyRoomDto("TestStudyRoom111", 10, 10000, true, true, true);
        StudyRoomResponseDto andSave = studyRoomService.createAndSave(savedCompany.getId(), studyRoomDto, file);
    }

//    @PostConstruct
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
