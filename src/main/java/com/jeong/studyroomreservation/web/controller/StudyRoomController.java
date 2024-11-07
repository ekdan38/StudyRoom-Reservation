package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoomMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.CompanyService;
import com.jeong.studyroomreservation.domain.service.StudyRoomService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.studyroom.StudyRoomRequestDto;
import com.jeong.studyroomreservation.web.dto.studyroom.StudyRoomUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "[StudyRoomController]")
@RequestMapping("/api/studyrooms/{companyId}")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;
    private final CompanyService companyService;
    private final StudyRoomMapper studyRoomMapper;

    //스터디 룸 생성
    // StudyRoomRequestDto

    /**
     * Role이 STUDYROOM_ADMIN이라면 검사해야됨.
     */
    @PostMapping
    public ResponseEntity<?> createStudyRoom(@PathVariable("companyId") Long companyId,
                                             @RequestBody @Validated StudyRoomRequestDto requestDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal UserDto userDto) {
        // STUDYROOM_ADMIM이면 해당 company에 접근 권한이 있는지 확인.
        if (UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            Company company = companyService.findById(companyId);
            if (!company.getUser().getId().equals(userDto.getId())) {
                ResponseDto<String> responseBody =
                        new ResponseDto<>("Access Denied", "No Permission to modify this company");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }


        if (bindingResult.hasErrors()) {
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }
        StudyRoomDto studyRoomDto = studyRoomService.createAndSave(studyRoomMapper.requestToDto(requestDto), companyId);
        ResponseDto<StudyRoomDto> responseBody = new ResponseDto<>("Success", studyRoomDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 조회 여러개
    @GetMapping
    public ResponseEntity<ResponseDto<Page<StudyRoomDto>>> getStudyRooms(@PathVariable("companyId") Long companyId,
                                                                         Pageable pageable) {
        Page<StudyRoomDto> studyRooms = studyRoomService.getStudyRooms(companyId, pageable);
        ResponseDto<Page<StudyRoomDto>> responseBody = new ResponseDto<>("Success", studyRooms);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 조회 단건
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<StudyRoomDto>> getStudyRoom(@PathVariable("companyId") Long companyId,
                                                                  @PathVariable("id") Long id) {
        StudyRoomDto studyRoom = studyRoomService.getStudyRoom(companyId, id);
        ResponseDto<StudyRoomDto> responseBody = new ResponseDto<>("Success", studyRoom);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudyRoom(@RequestBody @Validated StudyRoomUpdateDto updateDto,
                                             @PathVariable("companyId") Long companyId,
                                             @PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserDto userDto) {
        // STUDYROOM_ADMIM이면
        if (UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            Company company = companyService.findById(companyId);
            if (!company.getUser().getId().equals(userDto.getId())) {
                ResponseDto<String> responseBody =
                        new ResponseDto<>("Access Denied", "No Permission to modify this company");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }
        StudyRoomDto studyRoomDto = studyRoomService.updateStudyRoom(studyRoomMapper.updateToDto(updateDto), companyId, id);
        ResponseDto<StudyRoomDto> responseBody
                = new ResponseDto<>("Success", studyRoomDto);
        return ResponseEntity.ok().body(responseBody);
    }


    //스터디 룸 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudyRoom(@PathVariable("companyId") Long companyId,
                                             @PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserDto userDto){
        // STUDYROOM_ADMIM이면
        if (UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            Company company = companyService.findById(companyId);
            if (!company.getUser().getId().equals(userDto.getId())) {
                ResponseDto<String> responseBody =
                        new ResponseDto<>("Access Denied", "No Permission to modify this company");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }
            studyRoomService.deleteStudyRoom(companyId, id);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete StudyRoom id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }

}
