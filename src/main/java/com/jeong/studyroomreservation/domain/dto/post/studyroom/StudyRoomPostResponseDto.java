package com.jeong.studyroomreservation.domain.dto.post.studyroom;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudyRoomPostResponseDto {

    private Long id;
    private String title;
    private String content;
    private List<String> images = new ArrayList<>();

    public StudyRoomPostResponseDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
