package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;

public class BusinessBaseException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessBaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}