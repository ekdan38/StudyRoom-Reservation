package com.jeong.studyroomreservation.web.exceptionhandler;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.CompanyNotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.PendingCompanyNotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.StudyRoomNotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.UserNotFoundException;
import com.jeong.studyroomreservation.web.dto.ErrorResponseDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j(topic = "ExceptionHandler")
public class GlobalExceptionHandler {

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ResponseDto> unrecognizedPropertyException (UnrecognizedPropertyException e){
        log.error("UnrecognizedPropertyException = {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ResponseDto("unrecognizedPropertyException", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> httpMessageNotReadableException (HttpMessageNotReadableException e){
        log.error("HttpMessageNotReadableException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
    }


    @ExceptionHandler(PendingCompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> pendingCompanyNotFoundException (PendingCompanyNotFoundException e){
        log.error("PendingCompanyNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.PENDING_COMPANY_NOT_FOUND);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> companyNotFoundException (CompanyNotFoundException e){
        log.error("CompanyNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.COMPANY_NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> userNotFoundException (UserNotFoundException e){
        log.error("UserNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(StudyRoomNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> studyRoomNotFoundException (StudyRoomNotFoundException e){
        log.error("StudyRoomNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.STUDY_ROOM_NOT_FOUND);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> sQLIntegrityConstraintViolationException (SQLIntegrityConstraintViolationException e){
        log.error("SQLIntegrityConstraintViolationException  = {}", e.getMessage());
        return createErrorResponseEntity(ErrorCode.INTERGRITY_CONSTRAIN_VIOLATION);
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponseDto.of(errorCode),
                errorCode.getStatus());
    }
}
