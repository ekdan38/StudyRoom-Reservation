package com.jeong.studyroomreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class StudyroomReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomReservationApplication.class, args);
    }

}
