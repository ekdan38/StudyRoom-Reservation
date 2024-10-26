package com.jeong.studyroomreservation.web.dto;

import lombok.Data;

@Data
public class ResponseDto<T>{
    private String message;
    private T data;

    public ResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
