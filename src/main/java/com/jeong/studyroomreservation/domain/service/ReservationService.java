package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.reservation.ReservationDto;
import com.jeong.studyroomreservation.domain.dto.reservation.ReservationGetResponseDto;
import com.jeong.studyroomreservation.domain.dto.reservation.ReservationResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.reservation.Reservation;
import com.jeong.studyroomreservation.domain.entity.stuydroom.RoomState;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.NotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.ReservationException;
import com.jeong.studyroomreservation.domain.repository.ReservationRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StudyRoomRepository studyRoomRepository;

    // 해당 date에 예약 정보 가져오기
    public List<LocalTime> getReservedTimes(Long studyRoomId, LocalDate date) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithCompany(studyRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        // 운영 시간 가져오기
        Company company = studyRoom.getCompany();
        LocalTime openingTime = company.getOpeningTime();
        LocalTime closingTime = company.getClosingTime();

        // 주어진 날짜에 이미 예약된 시간대 가져오기
        List<Reservation> reservations = reservationRepository.findByStudyRoomIdAndDate(studyRoomId, date);
        List<LocalTime> reservedTimes = reservations.stream()
                .flatMap(r -> getTimesInRange(r.getStartTime(), r.getEndTime()).stream())
                .collect(Collectors.toList());

        // 전체 가능한 시간대 생성
        List<LocalTime> availableTimes = getTimesInRange(openingTime, closingTime);

        // 예약된 시간을 제외한 가능 시간대 계산
        availableTimes.removeAll(reservedTimes);
        return availableTimes;
    }

    // 예약 처리
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public ReservationResponseDto createReservation(Long studyRoomId, LocalDate date, LocalTime startTime, LocalTime endTime, UserDto userDto) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithCompanyWithUser(studyRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        if (!studyRoom.getRoomState().name().equals(RoomState.AVAILABLE.name())){
            throw new ReservationException(ErrorCode.R_NOT_AVAILABLE);
        }
        // 운영 시간 가져오기
        Company company = studyRoom.getCompany();
        LocalTime openingTime = company.getOpeningTime();
        LocalTime closingTime = company.getClosingTime();

        if (startTime.isBefore(openingTime) || endTime.isAfter(closingTime)) {
            throw new ReservationException(ErrorCode.R_OUT_OF_OPERATION_HOURS);
        }

        List<Reservation> reservations = reservationRepository.findByStudyRoomIdAndDate(studyRoomId, date);
        List<LocalTime> reservedTimes = reservations.stream()
                .flatMap(r -> getTimesInRange(r.getStartTime(), r.getEndTime()).stream())
                .collect(Collectors.toList());

        // 요청된 시간대가 이미 예약된 시간대와 겹치는지 확인
        List<LocalTime> requestedTimes = getTimesInRange(startTime, endTime);
        for (LocalTime time : requestedTimes) {
            if (reservedTimes.contains(time)) {
                throw new ReservationException(ErrorCode.R_ALREADY_RESERVED);
            }
        }
        ReservationDto reservationDto = new ReservationDto(date, startTime, endTime);
        User user = company.getUser();
        Reservation reservation = Reservation.createReservation(reservationDto, user, studyRoom);
        Reservation savedReservation = reservationRepository.save(reservation);


        long between = ChronoUnit.MINUTES.between(startTime, endTime)/ 60;
        Integer studyRoomPrice = studyRoom.getPrice();
        long totalPrice = studyRoomPrice * ChronoUnit.MINUTES.between(startTime, endTime) / 60;

        return new ReservationResponseDto(
                savedReservation.getId(),
                user.getName(),
                startTime,
                endTime,
                between,
                company.getName(),
                company.getPhoneNumber(),
                company.getLocation(),
                studyRoom.getName(),
                studyRoomPrice,
                totalPrice
        );
    }

    // 예약 삭제
    @Transactional
    public void deleteReservation(Long studyRoomId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndStudyRoomId(reservationId, studyRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

        reservationRepository.delete(reservation);
    }


    // 예약 내역 단건 조회
    @Transactional(readOnly = true)
    public ReservationGetResponseDto getReservation(Long studyRoomId, Long reservationId){
        Reservation reservation = reservationRepository.findByIdAndStudyRoomIdWithStudyRoomAndCompanyAndUser(reservationId, studyRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

        User user = reservation.getUser();
        StudyRoom studyRoom = reservation.getStudyRoom();
        Company company = studyRoom.getCompany();

        long between = ChronoUnit.MINUTES.between(reservation.getStartTime(), reservation.getEndTime())/ 60;
        Long totalPrice = between * studyRoom.getPrice() ;

        ReservationGetResponseDto.CompanyInfo companyInfo =
                new ReservationGetResponseDto.CompanyInfo(company.getId(), company.getName(), company.getPhoneNumber(), company.getLocation());

        ReservationGetResponseDto.UserInfo userInfo =
                new ReservationGetResponseDto.UserInfo(user.getId(), user.getName(), user.getName(), user.getPhoneNumber()
                );
        ReservationGetResponseDto.StudyRoomInfo studyRoomInfo =
                new ReservationGetResponseDto.StudyRoomInfo(studyRoom.getId(), studyRoom.getName(), studyRoom.getPrice());
        return new ReservationGetResponseDto(reservation.getId(), userInfo, companyInfo, studyRoomInfo, reservation.getStartTime(), reservation.getEndTime(), between, totalPrice);
    }

    // 주어진 시간 범위 내의 60분 단위 시간 리스트 생성
    private List<LocalTime> getTimesInRange(LocalTime startTime, LocalTime endTime) {
        List<LocalTime> times = new ArrayList<>();
        LocalTime time = startTime;

        while (time.isBefore(endTime)) {
            times.add(time);
            time = time.plus(60, ChronoUnit.MINUTES);
        }

        return times;
    }


}


