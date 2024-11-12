package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(com.jeong.studyroomreservation.UserAuditorAware.class)
class StudyRoomPostRepositoryTest {

    @Autowired
    StudyRoomPostRepository studyRoomPostRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    StudyRoomRepository studyRoomRepository;


    @Test
    @Transactional
    @DisplayName("StudyRoomPost 페이징 조회")
    public void findAllByStudyRoomId(){
        //given
        Long studyRoomId = createAndSaveStudyRoomPosts();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        //when
        Page<StudyRoomPost> page = studyRoomPostRepository.findAllByStudyRoomId(studyRoomId, pageRequest);

        //then
        List<StudyRoomPost> content = page.getContent(); //조회된 데이터
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(4); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 단건 조회")
    public void findByStudyRoomIdAndId(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
        Long studyRoomId = studyRoom.getId();

        StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title", "Content");
        StudyRoomPost studyRoomPost = studyRoomPostRepository.save(StudyRoomPost.createStudyRoomPost(studyRoomPostDto, studyRoom));
        Long studyRoomPostId = studyRoomPost.getId();

        //when
        Optional<StudyRoomPost> optionalStudyRoomPost = studyRoomPostRepository.findByStudyRoomIdAndIdWithStudyRoomPostFiles(studyRoomId, studyRoomPostId);

        //then
        assertThat(optionalStudyRoomPost).isPresent();
    }

    private Long createAndSaveStudyRoomPosts(){
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);
        StudyRoom studyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));

        for (int i = 0; i < 4; i++) {
            StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title" + (i + 1), "Content" + (i + 1));
            StudyRoomPost studyRoomPost = studyRoomPostRepository.save(StudyRoomPost.createStudyRoomPost(studyRoomPostDto, studyRoom));
        }
        return studyRoom.getId();
    }

}