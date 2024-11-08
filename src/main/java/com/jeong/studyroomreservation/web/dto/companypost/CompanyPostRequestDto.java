package com.jeong.studyroomreservation.web.dto.companypost;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyPostRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
