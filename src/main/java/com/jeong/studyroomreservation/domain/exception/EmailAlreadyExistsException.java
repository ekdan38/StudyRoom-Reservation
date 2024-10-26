package com.jeong.studyroomreservation.domain.exception;

public class EmailAlreadyExistsException extends IllegalArgumentException {
    public EmailAlreadyExistsException(String s) {
        super(s);
    }
}
