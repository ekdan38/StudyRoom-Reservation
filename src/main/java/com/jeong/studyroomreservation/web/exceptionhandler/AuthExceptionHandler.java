package com.jeong.studyroomreservation.web.exceptionhandler;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.ErrorResponse;
import com.jeong.studyroomreservation.domain.error.exception.EmailAlreadyExistsException;
import com.jeong.studyroomreservation.domain.error.exception.LoginIdAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j(topic = "authExceptionHandler")
public class AuthExceptionHandler {

    @ExceptionHandler(LoginIdAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> loginIdAlreadyExists(LoginIdAlreadyExistsException e){
        log.error("LoginIdAlreadyExistsException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.LOGINID_ALREADY_EXISTS);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> EmailAlreadyExists(Exception e){
        log.error("EmailAlreadyExistsException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponse.of(errorCode),
                errorCode.getStatus());
    }
}
