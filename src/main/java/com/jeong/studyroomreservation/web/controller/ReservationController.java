package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.reservation.ReservationGetResponseDto;
import com.jeong.studyroomreservation.domain.dto.reservation.ReservationResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.service.ReservationService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.reservation.ReservationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studyrooms/{studyRoomId}/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    //예약 가능 시간대 조회
    // ROLE: USER 이상 조회 가능
    @GetMapping
    public ResponseEntity<ResponseDto<List<LocalTime>>> getReservedTimes(@PathVariable("studyRoomId") Long studyRoomId,
                                                                         @RequestParam LocalDate date) {
        List<LocalTime> reservedTimes = reservationService.getReservedTimes(studyRoomId, date);
        ResponseDto<List<LocalTime>> responseBody = new ResponseDto<>("Success", reservedTimes);
        return ResponseEntity.ok().body(responseBody);
    }

    //예약 요청
    // ROLE: USER이상
    @PostMapping
    public ResponseEntity<ResponseDto<ReservationResponseDto>> createReservation(@PathVariable("studyRoomId") Long studyRoomId,
                                                                                 @RequestBody ReservationRequestDto requestDto,
                                                                                 @AuthenticationPrincipal UserDto userDto) {
        ReservationResponseDto responseDto = reservationService.createReservation(studyRoomId,
                requestDto.getDate(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                userDto);
        ResponseDto<ReservationResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //예약 거절(삭제)
    // STUDYROOM_ADMIN 이상
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ResponseDto<String>> deleteReservation(@PathVariable("studyRoomId") Long studyRoomId,
                                                                 @PathVariable("reservationId") Long reservationId) {

        reservationService.deleteReservation(studyRoomId, reservationId);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete reservation id = " + reservationId);
        return ResponseEntity.ok().body(responseBody);
    }

    // 예약 조회
    // STUDYROOM_ADMIN 이상
    @GetMapping("/{reservationId}")
    public ResponseEntity< ResponseDto<ReservationGetResponseDto>> getReservations(@PathVariable("studyRoomId") Long studyRoomId,
                                                                                        @PathVariable("reservationId") Long reservationId) {
        ReservationGetResponseDto responseDto = reservationService.getReservation(studyRoomId, reservationId);

        ResponseDto<ReservationGetResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }


}
