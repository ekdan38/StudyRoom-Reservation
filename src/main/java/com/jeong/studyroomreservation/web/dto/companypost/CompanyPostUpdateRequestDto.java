package com.jeong.studyroomreservation.web.dto.companypost;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CompanyPostUpdateRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<String> deleteImages;
}
