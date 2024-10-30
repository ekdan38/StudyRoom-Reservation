package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;

public class PhoneNumberAlreadyExistsException extends BusinessBaseException{
    public PhoneNumberAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
