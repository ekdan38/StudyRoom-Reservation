package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoomMapper;
import com.jeong.studyroomreservation.domain.service.StudyRoomService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.StudyRoomRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "[StudyRoomController]")
@RequestMapping("/api/studyrooms")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;
    private final StudyRoomMapper studyRoomMapper;
    private final ModelMapper modelMapper;

    //스터디 룸 생성
    // StudyRoomRequestDto
    @PostMapping
    public ResponseEntity<ResponseDto<?>> createStudyRoom(@RequestBody @Validated StudyRoomRequestDto requestDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal UserDto userDto){
        if(bindingResult.hasErrors()){
            log.error("validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }

        StudyRoomDto savedStudyRoomDto = studyRoomService.save(modelMapper.map(requestDto, StudyRoomDto.class), userDto);
        ResponseDto<StudyRoomDto> responseBody =
                new ResponseDto<>("Create StudyRoom Success", savedStudyRoomDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 조회 여러개
    @GetMapping("/{companyId}")
    public ResponseEntity<ResponseDto<Page<StudyRoomDto>>> getStudyRooms(@PathVariable("companyId")Long companyId,
                                                                         Pageable pageable){

        // 스터디 룸 페이징 조회(여러 건)
        Page<StudyRoomDto> pageDto = studyRoomService.getStudyRooms(pageable, companyId);
        ResponseDto<Page<StudyRoomDto>> responseBody =
                new ResponseDto<>("Get StudyRooms Success", pageDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 조회 단건
    @GetMapping("/{companyId}/{id}")
    public ResponseEntity<ResponseDto<StudyRoomDto>> getStudyRoom(@PathVariable("companyId")Long companyId,
                                                                  @PathVariable("id") Long id){
        // 스터디 룸 단건 조회
        StudyRoomDto studyRoomDto = studyRoomService.getStudyRoom(id);
        ResponseDto<StudyRoomDto> responseBody =
                new ResponseDto<>("Get StudyRoom Success", studyRoomDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 수정
    @PutMapping("/{companyId}/{id}")
    public ResponseEntity<ResponseDto<StudyRoomDto>> updateStudyRoom(@PathVariable("companyId")Long companyId,
                                                                     @PathVariable("id") Long id,
                                                                     StudyRoomRequestDto requestDto){
        StudyRoomDto studyRoomDto = studyRoomService.updateStudyRoom(id, modelMapper.map(requestDto, StudyRoomDto.class));
        ResponseDto<StudyRoomDto> responseBody = new ResponseDto<>("Update Success", studyRoomDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //스터디 룸 삭제
    @DeleteMapping("/{companyId}/{id}")
    public ResponseEntity<ResponseDto<String>> deleteStudyRoom(@PathVariable("companyId")Long companyId,
                                                               @PathVariable("id") Long id){
        studyRoomService.deleteStudyRoom(id);
        ResponseDto<String> responseBody = new ResponseDto<>("Delete Success", "StudyRoom id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }
}
