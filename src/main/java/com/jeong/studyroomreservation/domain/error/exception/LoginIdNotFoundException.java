package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;

public class LoginIdNotFoundException extends BusinessBaseException{
    public LoginIdNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
