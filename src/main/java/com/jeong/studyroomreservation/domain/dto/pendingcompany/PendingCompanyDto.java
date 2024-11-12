package com.jeong.studyroomreservation.domain.dto.pendingcompany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private LocalTime openingTime;

    private LocalTime closingTime;

    public PendingCompanyDto(Long userId, String name, String description, String location, String phoneNumber, LocalTime openingTime, LocalTime closingTime) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public PendingCompanyDto(String name, String description, String location, String phoneNumber, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
