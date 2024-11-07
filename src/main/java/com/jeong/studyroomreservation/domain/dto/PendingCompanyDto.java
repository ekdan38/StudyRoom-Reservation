package com.jeong.studyroomreservation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingCompanyDto {

    private Long id;

    @JsonIgnore
    private Long userId;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    public PendingCompanyDto(Long userId, String name, String description, String location, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }
}
