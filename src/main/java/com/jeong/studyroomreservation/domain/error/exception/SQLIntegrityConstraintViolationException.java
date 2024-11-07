package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.base.BusinessBaseException;

public class SQLIntegrityConstraintViolationException extends BusinessBaseException {
    public SQLIntegrityConstraintViolationException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
