package com.jeong.studyroomreservation.domain.dto.company;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyResponseDto {
    private Long id;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private List<String> images = new ArrayList<>();

    public CompanyResponseDto(Long id, String name, String description, String location, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }
}
