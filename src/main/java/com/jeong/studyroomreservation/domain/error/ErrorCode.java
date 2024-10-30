package com.jeong.studyroomreservation.domain.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_01", "Exists Email"),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_02", "Exists Username"),
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_03", "Exists PhoneNumber");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
