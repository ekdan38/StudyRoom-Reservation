package com.jeong.studyroomreservation.web.dto;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDto {

    private String message;
    private String code;


    private ErrorResponseDto(final ErrorCode code) {
        this.message = code.getMessage();
        this.code = code.getCode();
    }

    private ErrorResponseDto(final ErrorCode code, final String message) {
        this.message = message;
        this.code = code.getCode();
    }

    public static ErrorResponseDto of(final ErrorCode code) {
        return new ErrorResponseDto(code);
    }

    public static ErrorResponseDto of(final ErrorCode code, final String message) {
        return new ErrorResponseDto(code, message);
    }
}