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
    INTERGRITY_CONSTRAIN_VIOLATION (HttpStatus.BAD_REQUEST, "REQ_06", "Invalid Request"),
    COMPANY_POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_07", "Not Found CompanyPost"),
    STUDY_ROOM_POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQ_08", "Not Found StudyRoomPost"),

    R_OUT_OF_OPERATION_HOURS(HttpStatus.BAD_REQUEST, "R_01", "Not An Operation Time"),
    R_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "R_02", "StudyRoom Already Reserved"),
    R_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "R_03", "StudyRoom's State is Not AVAILABLE"),

    S3_EMPTY_FILE (HttpStatus.BAD_REQUEST, "S3_01", "Empty File"),
    S3_EXCEPTION_ON_IMAGE_UPLOAD (HttpStatus.BAD_REQUEST, "S3_02", "ImageUpload Fail"),
    S3_NO_FILE_EXTENTION (HttpStatus.BAD_REQUEST, "S3_03", "No Extention"),
    S3_INVALID_EXTENTION(HttpStatus.BAD_REQUEST, "S3_04", "Invalid Extention"),
    S3_EXCEPTION_PUT_OBJECT(HttpStatus.BAD_REQUEST, "S3_05", "PutObject Fail"),
    S3_EXCEPTION_DELETE(HttpStatus.BAD_REQUEST, "S3_06", "Delete Fail"),
    S3_EXCEPTION_SAVE_DB(HttpStatus.BAD_REQUEST, "S3_07", "Save DataBase Fail"),
    S3_EXCEPTION_UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "S3_08", "UnSupported Type");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
