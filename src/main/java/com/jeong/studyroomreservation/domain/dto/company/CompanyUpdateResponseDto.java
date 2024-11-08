package com.jeong.studyroomreservation.domain.dto.company;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyUpdateResponseDto {

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private List<String> newImages = new ArrayList<>();

    private List<String> deleteImages = new ArrayList<>();

    public CompanyUpdateResponseDto(String name, String description, String location, String phoneNumber) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }
}
