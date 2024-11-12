package com.jeong.studyroomreservation.web.exceptionhandler;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.*;
import com.jeong.studyroomreservation.web.dto.ErrorResponseDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.jeong.studyroomreservation.domain.error.ErrorCode.*;

@RestControllerAdvice
@Slf4j(topic = "ExceptionHandler")
public class GlobalExceptionHandler {

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponseDto> s3Exception (S3Exception e){
        log.error("S3Exception = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ResponseDto> unrecognizedPropertyException (UnrecognizedPropertyException e){
        log.error("UnrecognizedPropertyException = {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ResponseDto("unrecognizedPropertyException", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> httpMessageNotReadableException (HttpMessageNotReadableException e){
        log.error("HttpMessageNotReadableException  = {}", e.getMessage());
        return createErrorResponseEntity(HTTP_MESSAGE_NOT_READABLE);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> NotFoundException (NotFoundException e){
        log.error("NotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ErrorResponseDto> reservationException (ReservationException e){
        log.error("ReservationException  = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> sQLIntegrityConstraintViolationException (SQLIntegrityConstraintViolationException e){
        log.error("SQLIntegrityConstraintViolationException  = {}", e.getMessage());
        return createErrorResponseEntity(INTERGRITY_CONSTRAIN_VIOLATION);
    }



    private ResponseEntity<ErrorResponseDto> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponseDto.of(errorCode),
                errorCode.getStatus());
    }
}
