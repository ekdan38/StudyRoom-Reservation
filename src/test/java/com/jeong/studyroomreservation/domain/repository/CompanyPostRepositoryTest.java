package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
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
class CompanyPostRepositoryTest {

    @Autowired
    CompanyPostRepository companyPostRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Test
    @Transactional
    @DisplayName("CompanyPost 페이징 조회")
    public void findAllByCompanyId(){
        //given
        Long companyId = createAndSaveCompanyPosts();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        //when
        Page<CompanyPost> page = companyPostRepository.findAllByCompanyId(companyId, pageRequest);

        //then
        List<CompanyPost> content = page.getContent(); //조회된 데이터
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(4); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @DisplayName("CompanyPost 단건 조회")
    public void findByCompanyIdAndId(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon",
                "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));
        Long companyId = company.getId();

        CompanyPostDto companyPostDto = new CompanyPostDto("Title", "Content");
        CompanyPost companyPost = companyPostRepository.save(CompanyPost.createCompanyPost(companyPostDto, company));
        Long companyPostId = companyPost.getId();

        //when
        Optional<CompanyPost> optionalCompanyPost = companyPostRepository.findByCompanyIdAndIdWithCompanyPostFiles(companyId, companyPostId);

        //then
        assertThat(optionalCompanyPost).isPresent();
    }


    private Long createAndSaveCompanyPosts(){
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000"
                , LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        for (int i = 0; i < 4; i++) {
            CompanyPostDto companyPostDto = new CompanyPostDto("Title" + (i + 1), "Content" + (i + 1));
            companyPostRepository.save(CompanyPost.createCompanyPost(companyPostDto, company));
        }
        return company.getId();
    }

    private void createAndSaveCompanyPost(){
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));

        CompanyPostDto companyPostDto = new CompanyPostDto("Title", "Content");
        companyPostRepository.save(CompanyPost.createCompanyPost(companyPostDto, company));
    }



}