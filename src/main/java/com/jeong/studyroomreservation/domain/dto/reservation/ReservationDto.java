package com.jeong.studyroomreservation.domain.dto.reservation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationDto {

    private Long id;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private Long studyRoomId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    public ReservationDto(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
