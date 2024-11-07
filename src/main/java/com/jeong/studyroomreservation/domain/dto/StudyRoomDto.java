package com.jeong.studyroomreservation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Long companyId;

    private String name;

    private Integer capacity;

    private Integer price;

    private RoomState roomState;

    private Boolean tv;

    private Boolean wifi;

    private Boolean whiteBoard;

    public StudyRoomDto(Long companyId, String name, Integer capacity, Integer price, RoomState roomState, Boolean tv, Boolean wifi, Boolean whiteBoard) {
        this.companyId = companyId;
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.roomState = roomState;
        this.tv = tv;
        this.wifi = wifi;
        this.whiteBoard = whiteBoard;
    }

    public StudyRoomDto(String name, Integer capacity, Integer price, Boolean tv, Boolean wifi, Boolean whiteBoard) {
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.tv = tv;
        this.wifi = wifi;
        this.whiteBoard = whiteBoard;
    }

    public StudyRoomDto(String name, Integer capacity, Integer price, RoomState roomState, Boolean tv, Boolean wifi, Boolean whiteBoard) {
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.roomState = roomState;
        this.tv = tv;
        this.wifi = wifi;
        this.whiteBoard = whiteBoard;
    }
}
