package com.jeong.studyroomreservation.domain.error;

import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNAUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "AUTH_01", "UnAuthenticated User"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_02", "Exists Email"),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_03", "Exists Username"),
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_04", "Exists PhoneNumber"),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "AUTH_05", "Invalid Signature"),

    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "REQ_01", "Invalid HttpMessage"),
    PENDING_COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_02", "Not Found PendingCompany"),
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_03", "Not Found Company"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_04", "Not Found USER"),
    STUDY_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_05", "Not Found StudyRoom"),
    INTERGRITY_CONSTRAIN_VIOLATION (HttpStatus.BAD_REQUEST, "REQ_06", "Invalid Request");



    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
