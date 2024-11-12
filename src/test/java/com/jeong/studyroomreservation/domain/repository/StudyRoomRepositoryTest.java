package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(com.jeong.studyroomreservation.UserAuditorAware.class)
@Transactional
class StudyRoomRepositoryTest {

    @Autowired
    StudyRoomRepository studyRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    private User user;
    private Company company;
    private StudyRoom studyRoom;
    private Long studyRoomId;
    private Long companyId;

    @BeforeEach
    public void setUp(){
        UserDto userDto = new UserDto("username1", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company1", "description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        company = companyRepository.save(Company.createCompany(companyDto, user));
        companyId = company.getId();

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom1", 5, 10_000, true, false, true);
        studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
        studyRoomId = studyRoom.getId();

        createAndSaveStudyRoom();
    }

    private void createAndSaveStudyRoom(){
        for(int i = 0; i < 4; i++){
            StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom" + (i + 1), 5, 10_000, true, false, true);
            studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
        }
    }

    @Test
    @DisplayName("Company Id로 StudyRooms 페이징 조회")
    public void findAllByCompanyId() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        //when
        Page<StudyRoom> page = studyRoomRepository.findAllByCompanyId(companyId, pageRequest);

        //then
        List<StudyRoom> content = page.getContent(); //조회된 데이터
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(3); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @DisplayName("Company Id와 StudyRoom Id로 단일 StudyRoom 조회")
    public void findByCompanyIdAndId(){
        //given && when
        Optional<StudyRoom> foundStudyRoom = studyRoomRepository.findByCompanyIdAndIdWithStudyRoomFiles(companyId, studyRoomId);
        StudyRoom studyRoom = foundStudyRoom.get();

        //then
        assertThat(studyRoom).isNotNull();
        assertThat(studyRoom.getId()).isEqualTo(studyRoomId);
    }

    @Test
    @DisplayName("StudyRoom 과 Company조회, Fetch Join")
    public void findByIdWithCompany(){
        //given && when
        Optional<StudyRoom> foundStudyRoom = studyRoomRepository.findByIdWithCompany(studyRoomId);
        StudyRoom studyRoom = foundStudyRoom.get();

        //then
        assertThat(studyRoom).isNotNull();
        assertThat(studyRoom.getId()).isEqualTo(studyRoomId);
        assertThat(studyRoom.getCompany().getId()).isEqualTo(companyId);
    }

}