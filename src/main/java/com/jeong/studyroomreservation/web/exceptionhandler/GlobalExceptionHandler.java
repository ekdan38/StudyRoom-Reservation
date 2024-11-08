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


    @ExceptionHandler(PendingCompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> pendingCompanyNotFoundException (PendingCompanyNotFoundException e){
        log.error("PendingCompanyNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(PENDING_COMPANY_NOT_FOUND);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> companyNotFoundException (CompanyNotFoundException e){
        log.error("CompanyNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(COMPANY_NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> userNotFoundException (UserNotFoundException e){
        log.error("UserNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(USER_NOT_FOUND);
    }

    @ExceptionHandler(StudyRoomNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> studyRoomNotFoundException (StudyRoomNotFoundException e){
        log.error("StudyRoomNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(STUDY_ROOM_NOT_FOUND);
    }

    @ExceptionHandler(CompanyPostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> companyPostNotFoundException (CompanyPostNotFoundException e){
        log.error("CompanyPostNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(COMPANY_POST_NOT_FOUND);
    }

    @ExceptionHandler(StudyRoomPostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> studyRoomPostNotFoundException (StudyRoomPostNotFoundException e){
        log.error("StudyRoomPostNotFoundException  = {}", e.getMessage());
        return createErrorResponseEntity(COMPANY_POST_NOT_FOUND);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> sQLIntegrityConstraintViolationException (SQLIntegrityConstraintViolationException e){
        log.error("SQLIntegrityConstraintViolationException  = {}", e.getMessage());
        return createErrorResponseEntity(INTERGRITY_CONSTRAIN_VIOLATION);
    }

    @ExceptionHandler(FileUnsupportedEntityException.class)
    public ResponseEntity<ErrorResponseDto> fileUnsupportedEntityException (FileUnsupportedEntityException e){
        log.error("FileUnsupportedEntityException  = {}", e.getMessage());
        return createErrorResponseEntity(FILE_UNSUPPORTED_ENTITY);
    }


    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponseDto> s3Exception (S3Exception e){
        log.error("S3Exception  = {}", e.getMessage());
        if (e.getErrorCode().equals(S3_EMPTY_FILE)){
            return createErrorResponseEntity(S3_EMPTY_FILE);
        }
        else if(e.getErrorCode().equals(S3_EXCEPTION_ON_IMAGE_UPLOAD)){
            return createErrorResponseEntity(S3_EXCEPTION_ON_IMAGE_UPLOAD);
        }
        else if(e.getErrorCode().equals(S3_NO_FILE_EXTENTION)){
            return createErrorResponseEntity(S3_NO_FILE_EXTENTION);
        }
        else if(e.getErrorCode().equals(S3_INVALID_EXTENTION)){
            return createErrorResponseEntity(S3_INVALID_EXTENTION);
        }
        else if(e.getErrorCode().equals(S3_EXCEPTION_PUT_OBJECT)){
            return createErrorResponseEntity(S3_EXCEPTION_PUT_OBJECT);
        }
        else {
            return createErrorResponseEntity(S3_EXCEPTION_DELETE);
        }
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ErrorResponseDto.of(errorCode),
                errorCode.getStatus());
    }
}
