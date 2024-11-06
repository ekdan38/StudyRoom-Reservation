package com.jeong.studyroomreservation.web.exceptionhandler;

import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.PhoneNumberAlreadyExistsException;
import com.jeong.studyroomreservation.web.dto.ErrorResponseDto;
import com.jeong.studyroomreservation.domain.error.exception.EmailAlreadyExistsException;
import com.jeong.studyroomreservation.domain.error.exception.UsernameAlreadyExistsException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j(topic = "[authExceptionHandler]")
public class AuthExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> loginIdAlreadyExists(UsernameAlreadyExistsException e){
        log.error("LoginIdAlreadyExistsException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> EmailAlreadyExists(EmailAlreadyExistsException e){
        log.error("EmailAlreadyExistsException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> PhoneNumberAlreadyExists(PhoneNumberAlreadyExistsException e){
        log.error("PhoneNumberAlreadyExistsException = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
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
