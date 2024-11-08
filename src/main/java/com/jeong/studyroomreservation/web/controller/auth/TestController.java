package com.jeong.studyroomreservation.web.controller.auth;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final CompanyRepository companyRepository;


    @RequestMapping("/api/test")
    public ResponseEntity<?> test (@AuthenticationPrincipal UserDto userDto){
        Boolean authenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", authenticated);
        ResponseDto<Map<String, Object>> responseBody = new ResponseDto<>("Authorization Success", response);
        return ResponseEntity.ok().body(responseBody);
    }


    @GetMapping("/api/test1")
        public ResponseEntity<?> test1(){
        List<Company> all = companyRepository.findAll();
        for (Company company : all) {
            List<StudyRoom> studyRooms = company.getStudyRooms();
            for (StudyRoom studyRoom : studyRooms) {
                System.out.println("studyRoom = " + studyRoom);
                System.out.println("studyRoom.getName() = " + studyRoom.getName());
            }
        }
        return ResponseEntity.ok().build();
    }

}
