package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStudyRoomIdAndDate(Long studyRoomId, LocalDate date);

    Optional<Reservation> findByIdAndStudyRoomId(Long id, Long studyRoomId);

    Page<Reservation> findByIdAndStudyRoomId(Long id, Long studyRoomId, Pageable pageable);


    @Query("SELECT r FROM Reservation r JOIN FETCH r.studyRoom JOIN FETCH r.user JOIN FETCH r.studyRoom.company " +
            "WHERE r.id = :id AND r.studyRoom.id = :studyRoomId")
    Optional<Reservation> findByIdAndStudyRoomIdWithStudyRoomAndCompanyAndUser(@Param("id") Long id,
                                                                        @Param("studyRoomId") Long studyRoomId);

}

