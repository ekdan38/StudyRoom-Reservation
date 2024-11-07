package com.jeong.studyroomreservation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingCompanyWithUserDto {

    private PendingCompanyDto pendingCompany;
    private UserDto user;
}
