package com.jeong.studyroomreservation.domain.dto.post.company;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyPostDto {

    private Long id;
    private String title;
    private String content;

    public CompanyPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
