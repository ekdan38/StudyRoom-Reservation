package com.jeong.studyroomreservation.web.dto.studyroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyRoomRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer capacity;

    @NotNull
    private Integer price;

    @NotNull
    private Boolean tv;

    @NotNull
    private Boolean wifi;

    @NotNull
    private Boolean whiteBoard;
}
