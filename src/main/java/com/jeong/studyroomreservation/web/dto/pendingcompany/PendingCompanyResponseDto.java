package com.jeong.studyroomreservation.web.dto.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingCompanyResponseDto {

    private PendingCompanyDto pendingCompanyDto;
    private Long userId;
}
