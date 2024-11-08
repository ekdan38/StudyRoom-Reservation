package com.jeong.studyroomreservation.domain.dto.studyroom;

import com.jeong.studyroomreservation.domain.entity.stuydroom.RoomState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudyRoomResponseDto {

    private Long id;

    private String name;

    private Integer capacity;

    private Integer price;

    private RoomState roomState;

    private Boolean tv;

    private Boolean wifi;

    private Boolean whiteBoard;

    private List<String> Images = new ArrayList<>();

    public StudyRoomResponseDto(Long id, String name, Integer capacity, Integer price, RoomState roomState, Boolean tv, Boolean wifi, Boolean whiteBoard) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.roomState = roomState;
        this.tv = tv;
        this.wifi = wifi;
        this.whiteBoard = whiteBoard;
    }
}
