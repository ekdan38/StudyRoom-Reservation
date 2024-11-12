package com.jeong.studyroomreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing(auditorAwareRef = "userAuditorAware")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class StudyroomReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomReservationApplication.class, args);
    }


}
