package com.jeong.studyroomreservation.domain.dto.reservation;

import com.jeong.studyroomreservation.domain.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ReservationGetResponseDto {
    private Long reservationId;
    private UserInfo userInfo;
    private CompanyInfo companyInfo;
    private StudyRoomInfo studyRoomInfo;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long useTime;
    private Long totalPrice;


    @Data
    @AllArgsConstructor
    public static class StudyRoomInfo{
        private Long studyRoomId;
        private String studyRoomName;
        private Integer StudyRoomPrice;
    }

    @Data
    @AllArgsConstructor
    public static class UserInfo{
        private Long userId;
        private String username;
        private String name;
        private String phoneNumber;
    }
    @Data
    @AllArgsConstructor
    public static class CompanyInfo{
        private Long companyId;
        private String name;
        private String phoneNumber;
        private String location;
    }

}