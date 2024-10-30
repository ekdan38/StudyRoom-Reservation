package com.jeong.studyroomreservation.web;

public abstract class TestConst {

    private static int USERNAME_CNT = 0;
    private static int EMAIL_CNT = 0;
    private static int PHONE_NUMBER_CNT = 0;

    public static String getUniqueUsername(){
        return "testusername" + (++USERNAME_CNT);
    }

    public static String getUniqueEmail(){
        return "test" + (++EMAIL_CNT) +"@gmail.com";
    }

    public static String getUniquePhoneNumber(){
        return "010-" + (100 + (++EMAIL_CNT)) +"-0000";
    }

}
