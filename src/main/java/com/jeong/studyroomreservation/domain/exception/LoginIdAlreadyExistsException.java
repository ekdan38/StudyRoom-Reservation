package com.jeong.studyroomreservation.domain.exception;

public class LoginIdAlreadyExistsException extends IllegalArgumentException{
    public LoginIdAlreadyExistsException(String s) {
        super(s);
    }
}
