package com.jeong.studyroomreservation.domain.dto.post.company;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyPostResponseDto {

    private Long id;
    private String title;
    private String content;
    private List<String> Images = new ArrayList<>();

    public CompanyPostResponseDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
