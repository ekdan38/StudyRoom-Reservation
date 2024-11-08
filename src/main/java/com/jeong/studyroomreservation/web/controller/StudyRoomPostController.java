package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPostMapper;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.StudyRoomPostService;
import com.jeong.studyroomreservation.domain.service.StudyRoomService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.studyroompost.StudyRoomPostRequestDto;
import com.jeong.studyroomreservation.web.dto.studyroompost.StudyRoomPostUpdateRequestDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "[StudyRoomPostController]")
@RequestMapping("/api/studyroom-post/{studyRoomId}")
public class StudyRoomPostController {

    private final StudyRoomPostService studyRoomPostService;
    private final StudyRoomService studyRoomService;
    private final StudyRoomPostMapper studyRoomPostMapper;

    // 스터디 룸 글 등록
   @PostMapping
   public ResponseEntity<?> createStudyRoomPost(@PathVariable("studyRoomId") Long studyRoomId,
                                                @RequestPart("file") List<MultipartFile> files,
                                                @RequestPart("data") @Validated StudyRoomPostRequestDto requestDto,
                                                BindingResult bindingResult,
                                                @AuthenticationPrincipal UserDto userDto){

       if (!checkPermission(userDto, studyRoomId)) {
           ResponseDto<String> responseBody =
                   new ResponseDto<>("Access Denied", "No Permission to modify this studyRoom");
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
       }

       if (bindingResult.hasErrors()) {
           log.error("Validation Error = {}", bindingResult);
           ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
           return ResponseEntity.badRequest().body(responseBody);
       }
       StudyRoomPostDto studyRoomPostDto = studyRoomPostMapper.requestToDto(requestDto);
       StudyRoomPostResponseDto responseDto = studyRoomPostService.createAndSave(studyRoomId, studyRoomPostDto, files);

       ResponseDto<StudyRoomPostResponseDto> responseBody = new ResponseDto<>("Success", responseDto);

       return ResponseEntity.ok().body(responseBody);
   }

    // 스터디 룸 글 여러건 조회
    @GetMapping
    public ResponseEntity<ResponseDto<Page<StudyRoomPostResponseDto>>> getStudyRoomPosts(@PathVariable("studyRoomId") Long studyRoomId,
                                               Pageable pageable){

        Page<StudyRoomPostResponseDto> responseDtos = studyRoomPostService.getStudyRoomPosts(studyRoomId, pageable);
        ResponseDto<Page<StudyRoomPostResponseDto>> responseBody = new ResponseDto<>("Success", responseDtos);
        return ResponseEntity.ok().body(responseBody);
    }


    // 스터디 룸 글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudyRoomPost(@PathVariable("studyRoomId") Long studyRoomId,
                                              @PathVariable("id") Long id){
        StudyRoomPostResponseDto responseDto = studyRoomPostService.getStudyRoomPost(studyRoomId, id);
        ResponseDto<StudyRoomPostResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }


    // 스터디 룸 글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudyRoomPost(@PathVariable("studyRoomId") Long studyRoomId,
                                                 @PathVariable("id") Long id,
                                                 @RequestPart("files") List<MultipartFile> files,
                                                 @RequestPart("data") @Validated StudyRoomPostUpdateRequestDto updateDto,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal UserDto userDto){
        if (!checkPermission(userDto, studyRoomId)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this studyRoom");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }

        StudyRoomPostDto studyRoomPostDto = studyRoomPostMapper.updateToDto(updateDto);
        StudyRoomPostUpdateResponseDto responseDto =
                studyRoomPostService.updateStudyRoomPost(studyRoomId, id, studyRoomPostDto, files, updateDto.getDeleteImages());
        ResponseDto<StudyRoomPostUpdateResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }

    // 스터디 룸 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudyRoomPost(@PathVariable("studyRoomId") Long studyRoomId,
                                                 @PathVariable("id") Long id,
                                                 @AuthenticationPrincipal UserDto userDto){

        if (!checkPermission(userDto, studyRoomId)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this studyRoom");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        studyRoomPostService.deleteStudyRoomPost(studyRoomId, id);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete StudyRoomPost id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }

    private Boolean checkPermission(UserDto userDto, Long studyRoomId) {
        if (UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            StudyRoom studyRoom = studyRoomService.findByIdWithCompany(studyRoomId);
            if (!studyRoom.getCompany().getUser().getId().equals(userDto.getId())) {
                return false;
            }
        }
        return true;
    }
}
