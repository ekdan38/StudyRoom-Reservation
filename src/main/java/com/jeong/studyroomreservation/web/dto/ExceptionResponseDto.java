package com.jeong.studyroomreservation.web.dto;

import lombok.Data;

@Data
public class ExceptionResponseDto {
    private Exception e;

    public ExceptionResponseDto(Exception e) {
        this.e = e;
    }

}
