package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.base.BusinessBaseException;

public class FileUnsupportedEntityException extends BusinessBaseException {
    public FileUnsupportedEntityException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
