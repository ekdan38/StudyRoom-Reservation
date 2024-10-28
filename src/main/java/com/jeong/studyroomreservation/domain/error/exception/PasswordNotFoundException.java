package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;

public class PasswordNotFoundException extends BusinessBaseException{
    public PasswordNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}