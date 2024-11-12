package com.jeong.studyroomreservation.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ReservationResponseDto {

    private Long reservationId;
    private String userName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long useTime;
    private String companyName;
    private String companyPhoneNumber;
    private String companyLocation;
    private String studyRoomName;
    private Integer StudyRoomPrice;
    private Long totalPrice;


}
