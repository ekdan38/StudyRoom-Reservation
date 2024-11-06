package com.jeong.studyroomreservation.domain.dto;

import com.jeong.studyroomreservation.domain.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingCompanyDto {

    private Long id;

    private UserDto userDto;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

}
