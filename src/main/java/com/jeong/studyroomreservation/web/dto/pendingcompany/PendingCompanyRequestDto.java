package com.jeong.studyroomreservation.web.dto.pendingcompany;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PendingCompanyRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    @Pattern(
            regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",  // 한국식 전화번호 형식 (예: 010-1234-5678)
            message = "전화번호는 010-1234-5678 형식이어야 합니다."
    )
    private String phoneNumber;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

}
