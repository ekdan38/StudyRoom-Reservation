package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.reservation.ReservationGetResponseDto;
import com.jeong.studyroomreservation.domain.dto.reservation.ReservationResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.reservation.Reservation;
import com.jeong.studyroomreservation.domain.entity.stuydroom.RoomState;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.error.exception.ReservationException;
import com.jeong.studyroomreservation.domain.repository.ReservationRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ReservationService reservationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    StudyRoomRepository studyRoomRepository;

    @Test
    @Transactional
    @DisplayName("스터디룸이 예약 실패, 예약 시간 존재함.")
    public void getReservedTimes_Fail_reservated(){
        //given
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        LocalTime alreadyStartTime = LocalTime.of(10, 0);
        LocalTime alreadyEndTime = LocalTime.of(12, 0);

        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(13, 0);

        UserDto userDto =
                new UserDto("username1", "password@",
                        "test", "teset@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto =
                new CompanyDto("Company1", "description", "location", "010-0000-0000", openingTime, closingTime);
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, true, true, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));

        LocalDate localDate = LocalDate.of(2024, 11, 12);

        StudyRoomDto studyRoomUpdateDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, RoomState.AVAILABLE, true, true, true);
        studyRoom.updateStudyRoom(studyRoomUpdateDto);

        em.flush();
        em.clear();

        //when && then
        reservationService.createReservation(studyRoom.getId(), localDate, alreadyStartTime, alreadyEndTime, userDto);

        assertThatThrownBy(() -> reservationService.createReservation(studyRoom.getId(), localDate, startTime, endTime, userDto))
                .isInstanceOf(ReservationException.class);
    }

    @Test
    @DisplayName("스터디룸이 예약 실패, 동시성 테스트")
    public void getReservedTimes_Fail_Interrupted() throws InterruptedException {
        // given
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(22, 0);
        LocalTime alreadyStartTime = LocalTime.of(10, 0);
        LocalTime alreadyEndTime = LocalTime.of(12, 0);

        UserDto userDto = new UserDto("username1", "password@", "test", "teset@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company1", "description", "location", "010-0000-0000", openingTime, closingTime);
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom1", 10, 10000, true, true, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
        Long studyRoomId = studyRoom.getId();

        LocalDate localDate = LocalDate.of(2024, 11, 12);

//        studyRoom.updateStudyRoom(new StudyRoomDto("StudyRoom1", 10, 10000, RoomState.AVAILABLE, true, true, true));

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<String> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    ReservationResponseDto reservation = reservationService.createReservation(studyRoomId, localDate, alreadyStartTime, alreadyEndTime, userDto);
                    results.add("success");
                } catch (Exception e) {
                    results.add("failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        List<Reservation> reservations = reservationRepository.findByStudyRoomIdAndDate(studyRoom.getId(), localDate);

        long successCount = results.stream().filter(result -> result.equals("success")).count();
        long failedCount = results.stream().filter(result -> result.startsWith("failed")).count();
        // 예약된 항목이 1개인지 확인
        assertThat(reservations.size()).isEqualTo(1);

        // 결과 확인
    }



    @Test
    @Transactional
    @DisplayName("예약 생성_예약 성공")
    public void createReservation_Success(){
        //given
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        UserDto userDto =
                new UserDto("username1", "password@",
                        "test", "teset@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto =
                new CompanyDto("Company1", "description", "location", "010-0000-0000", openingTime, closingTime);
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, true, true, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));

        LocalDate localDate = LocalDate.of(2024, 11, 12);

        StudyRoomDto studyRoomUpdateDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, RoomState.AVAILABLE, true, true, true);
        studyRoom.updateStudyRoom(studyRoomUpdateDto);

        em.flush();
        em.clear();

        //when
        ReservationResponseDto reservation = reservationService.createReservation(studyRoom.getId(), localDate, startTime, endTime, userDto);

        //then
        assertThat(reservation.getReservationId()).isNotNull();
        assertThat(reservation.getTotalPrice()).isEqualTo(20000);
        assertThat(reservation.getUserName()).isEqualTo(user.getName());
        assertThat(reservation.getCompanyName()).isEqualTo(company.getName());
    }

    @Test
    @Transactional
    @DisplayName("예약 단건 조회")
    public void getReservation(){
        //given
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        UserDto userDto =
                new UserDto("username10", "password@",
                        "test", "tese11@gmail.com", "010-0000-0011", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto =
                new CompanyDto("Company1", "description", "location", "010-0000-0000", openingTime, closingTime);
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, true, true, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));

        LocalDate localDate = LocalDate.of(2024, 11, 12);

        StudyRoomDto studyRoomUpdateDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, RoomState.AVAILABLE, true, true, true);
        studyRoom.updateStudyRoom(studyRoomUpdateDto);

        em.flush();
        em.clear();

        ReservationResponseDto reservation = reservationService.createReservation(studyRoom.getId(), localDate, startTime, endTime, userDto);
        //when
        ReservationGetResponseDto responseDto = reservationService.getReservation(studyRoom.getId(), reservation.getReservationId());

        //then
        assertThat(responseDto.getReservationId()).isNotNull();
        assertThat(responseDto.getTotalPrice()).isEqualTo(20000);
        assertThat(responseDto.getUserInfo().getName()).isEqualTo(user.getName());
        assertThat(responseDto.getCompanyInfo().getName()).isEqualTo(company.getName());
    }

    @Test
    @Transactional
    @DisplayName("예약 취소")
    public void deleteReservation(){
        //given
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        UserDto userDto =
                new UserDto("username1", "password@",
                        "test", "teset@gmail.com", "010-9707-1234", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto =
                new CompanyDto("Company1", "description", "location", "010-0000-0000", openingTime, closingTime);
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, true, true, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));

        LocalDate localDate = LocalDate.of(2024, 11, 12);

        StudyRoomDto studyRoomUpdateDto =
                new StudyRoomDto("StudyRoom1", 10, 10000, RoomState.AVAILABLE, true, true, true);
        studyRoom.updateStudyRoom(studyRoomUpdateDto);

        em.flush();
        em.clear();

        ReservationResponseDto reservation = reservationService.createReservation(studyRoom.getId(), localDate, startTime, endTime, userDto);
        //when
        reservationService.deleteReservation(studyRoom.getId(), reservation.getReservationId());

        //then
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservation.getReservationId());
        assertThat(optionalReservation).isNotPresent();
    }
}