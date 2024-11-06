package com.jeong.studyroomreservation.domain.dto;

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
    private UserDto userDto;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;


}
