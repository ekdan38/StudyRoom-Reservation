package com.jeong.studyroomreservation.domain.error.exception;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.base.BusinessBaseException;

public class S3Exception extends BusinessBaseException {
    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
