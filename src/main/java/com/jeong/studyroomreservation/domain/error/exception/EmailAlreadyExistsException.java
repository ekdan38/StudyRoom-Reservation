package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.base.BusinessBaseException;

public class EmailAlreadyExistsException extends BusinessBaseException {
    public EmailAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
