package com.jeong.studyroomreservation.web.dto.studyroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StudyRoomUpdateDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer capacity;

    @NotNull
    private Integer price;

    @NotNull
    @Pattern(regexp = "AVAILABLE|RESERVED|UNAVAILABLE", message = "AVAILABLE, RESERVED, UNAVAILABLE 중 값이 있어야 합니다.")
    private String state;

    @NotNull
    private Boolean tv;

    @NotNull
    private Boolean wifi;

    @NotNull
    private Boolean whiteBoard;

}
