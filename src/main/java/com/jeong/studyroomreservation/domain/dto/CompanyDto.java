package com.jeong.studyroomreservation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Long id;
    //사장님
    @JsonIgnore
    private Long userId;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    public CompanyDto(Long userId, String name, String description, String location, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    public CompanyDto(String name, String description, String location, String phoneNumber) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }
}
