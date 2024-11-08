package com.jeong.studyroomreservation.domain.dto.post.company;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyPostUpdateResponseDto {

    private Long id;
    private String title;
    private String content;
    private List<String> newImages = new ArrayList<>();
    private List<String> deleteImages = new ArrayList<>();

    public CompanyPostUpdateResponseDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
