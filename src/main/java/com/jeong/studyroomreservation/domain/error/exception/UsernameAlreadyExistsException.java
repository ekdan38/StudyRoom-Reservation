package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.base.BusinessBaseException;

public class UsernameAlreadyExistsException extends BusinessBaseException {

    public UsernameAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
