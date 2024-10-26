package com.jeong.studyroomreservation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeong.studyroomreservation.domain.entity.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String loginId;

    @JsonIgnore
    private String password;

    private String name;

    private String email;

    private String phoneNumber;

    private UserRole role;

    public UserDto(String loginId, String password, String name, String email, String phoneNumber, UserRole userRole) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = userRole;
    }

    public UserDto(Long id, String loginId, String password, String name, String email, String phoneNumber, UserRole role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
