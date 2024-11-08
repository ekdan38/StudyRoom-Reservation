package com.jeong.studyroomreservation.domain.entity.post.studyroom;

import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomUpdateResponseDto;
import com.jeong.studyroomreservation.web.dto.studyroompost.StudyRoomPostRequestDto;
import com.jeong.studyroomreservation.web.dto.studyroompost.StudyRoomPostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyRoomPostMapper {

    // entity => responseDto
    public StudyRoomPostResponseDto entityToResponse(StudyRoomPost entity) {
        return new StudyRoomPostResponseDto(entity.getId(), entity.getTitle(), entity.getContent());
    }

    // request => dto
    public StudyRoomPostDto requestToDto(StudyRoomPostRequestDto requestDto) {
        return new StudyRoomPostDto(requestDto.getTitle(), requestDto.getContent());
    }

    // response => updateResponse
    public StudyRoomPostUpdateResponseDto responseToUpdateResponse(StudyRoomPostResponseDto responseDto){
        StudyRoomPostUpdateResponseDto updateResponseDto = new StudyRoomPostUpdateResponseDto(responseDto.getId(), responseDto.getTitle(), responseDto.getContent());
        List<String> images = responseDto.getImages();
        for (String image : images) {
            updateResponseDto.getNewImages().add(image);
        }
        return updateResponseDto;
    }

    // update => dto
    public StudyRoomPostDto updateToDto(StudyRoomPostUpdateRequestDto requestDto){
        return new StudyRoomPostDto(requestDto.getTitle(), requestDto.getContent());
    }
}
