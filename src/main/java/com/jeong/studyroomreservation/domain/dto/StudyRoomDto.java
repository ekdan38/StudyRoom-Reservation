package com.jeong.studyroomreservation.domain.dto;

import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.RoomState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyRoomDto {

    private Long id;

    private CompanyDto companyDto;

    private String name;

    private Integer capacity;

    private Integer price;

    private RoomState roomState;

    private Boolean tv;

    private Boolean wifi;

    private Boolean whiteBoard;

}
