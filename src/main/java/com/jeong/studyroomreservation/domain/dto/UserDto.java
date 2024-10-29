package com.jeong.studyroomreservation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeong.studyroomreservation.domain.entity.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private String email;

    private String phoneNumber;

    private UserRole role;

    public UserDto(String username, String password, String name, String email, String phoneNumber, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = userRole;
    }

    public UserDto(Long id, String username, String password, String name, String email, String phoneNumber, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
