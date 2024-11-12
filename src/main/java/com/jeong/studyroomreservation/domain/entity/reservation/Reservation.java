package com.jeong.studyroomreservation.domain.entity.reservation;

import com.jeong.studyroomreservation.domain.dto.reservation.ReservationDto;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id")
    private StudyRoom studyRoom;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Reservation(User user, StudyRoom studyRoom, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.user = user;
        this.studyRoom = studyRoom;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public static Reservation createReservation(ReservationDto dto, User user, StudyRoom studyRoom){
        Reservation reservation = new Reservation(user, studyRoom, dto.getDate(), dto.getStartTime(), dto.getEndTime());
        return reservation;
    }

}
