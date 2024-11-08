package com.jeong.studyroomreservation.domain.dto.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingCompanyWithUserDto {

    private PendingCompanyDto pendingCompany;
    private UserDto user;
}
