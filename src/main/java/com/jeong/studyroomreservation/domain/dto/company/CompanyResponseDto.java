package com.jeong.studyroomreservation.domain.dto.company;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyResponseDto {
    private Long id;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private List<String> images = new ArrayList<>();

    public CompanyResponseDto(Long id, String name, String description, String location,
                              String phoneNumber, LocalTime openingTime, LocalTime closingTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;

    }
}
