package com.jeong.studyroomreservation.web.exceptionhandler;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.SignupException;
import com.jeong.studyroomreservation.web.dto.ErrorResponseDto;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j(topic = "[authExceptionHandler]")
public class AuthExceptionHandler {

    @ExceptionHandler(SignupException.class)
    public ResponseEntity<ErrorResponseDto> signupException(SignupException e){
        log.error("SignupException = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponseDto> SignatureException(SignatureException e){
        log.error("SignatureException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.INVALID_SIGNATURE);
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponseDto.of(errorCode),
                errorCode.getStatus());
    }
}
