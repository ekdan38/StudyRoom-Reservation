package com.jeong.studyroomreservation.domain.dto.post.studyroom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyRoomPostDto {


    private Long id;
    private String title;
    private String content;

    public StudyRoomPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
