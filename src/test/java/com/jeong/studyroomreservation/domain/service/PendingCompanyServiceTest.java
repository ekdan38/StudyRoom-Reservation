package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyWithUserDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.PendingCompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PendingCompanyServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PendingCompanyRepository pendingCompanyRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    PendingCompanyService pendingCompanyService;

    private Long userId;
    private Long pendingCompanyId;




    @Test
    @Transactional
    @DisplayName("PendingCompany 생성, 저장")
    public void createAndSave(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        Long userId = userRepository.save(User.createUser(userDto)).getId();

        PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(userId, "Company", "description", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(9, 0));

        //when
        PendingCompanyDto responseDto = pendingCompanyService.createAndSave(pendingCompanyDto);

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getUserId()).isEqualTo(userId);
        assertThat(responseDto.getName()).isEqualTo("Company");
    }

    @Test
    @Transactional
    @DisplayName("PendingCompany 페이징 조회")
    public void getPendingCompanies(){
        //given
        createAndSavePendingCompanies();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        //when
        Page<PendingCompanyWithUserDto> page = pendingCompanyService.getPendingCompanies(pageRequest);

        //then
        List<PendingCompanyWithUserDto> content = page.getContent();
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(14); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(7); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @DisplayName("PendingCompany 단건 조회")
    public void getPendingCompany(){
        //given
        createAndSavePendingCompanies();

        //when
        PendingCompanyWithUserDto responseDto = pendingCompanyService.getPendingCompany(pendingCompanyId);

        //then
        assertThat(responseDto.getPendingCompany().getId()).isEqualTo(pendingCompanyId);
        assertThat(responseDto.getUser().getId()).isEqualTo(userId);
    }

    @Test
    @Transactional
    @DisplayName("PendingCompany 수정")
    public void updatePendingCompany(){
        //given
        Long pendingCompanyId = createAndSavePendingCompany();

        //when
        PendingCompanyDto pendingCompanyUpdateDto = new PendingCompanyDto(userId, "Company3", "description3", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(9, 0));
        PendingCompanyDto responseDto = pendingCompanyService.updatePendingCompany(pendingCompanyId, pendingCompanyUpdateDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(pendingCompanyId);
        assertThat(responseDto.getName()).isEqualTo("Company3");
        assertThat(responseDto.getDescription()).isEqualTo("description3");
    }

    @Test
    @Transactional
    @DisplayName("PendingCompany 삭제")
    public void deletePendingCompany(){
        //given
        Long pendingCompanyId = createAndSavePendingCompany();

        //when
        pendingCompanyService.deletePendingCompany(pendingCompanyId);
        Optional<PendingCompany> foundPendingCompany = pendingCompanyRepository.findById(pendingCompanyId);

        //then
        assertThat(foundPendingCompany).isNotPresent();
    }

    @Test
    @Transactional
    @DisplayName("PendingCompany 승인(Company로 이동.)")
    public void approvalPendingCompany(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = User.createUser(userDto);
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(userId, "Company", "description", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(9, 0));
        PendingCompany savedPendingCompany = pendingCompanyRepository.save(PendingCompany.createPendingCompany(pendingCompanyDto, user));

        Long pendingCompanyId = savedPendingCompany.getId();

        //when
        CompanyDto companyDto = pendingCompanyService.approvalPendingCompany(pendingCompanyId);
        Long companyId = companyDto.getId();
        Company company = companyRepository.findById(companyId).get();

        Optional<PendingCompany> foundPendingCompany = pendingCompanyRepository.findById(pendingCompanyId);

        User updateUser = userRepository.findById(userId).get();

        //then
        assertThat(foundPendingCompany).isNotPresent();
        assertThat(updateUser.getRole()).isEqualTo(UserRole.ROLE_STUDYROOM_ADMIN);
        assertThat(companyDto.getId()).isEqualTo(company.getId());
        assertThat(companyDto.getName()).isEqualTo(pendingCompanyDto.getName());

    }


    private Long createAndSavePendingCompany(){
        UserDto userDto = new UserDto("username", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));
        Long userId = user.getId();

        PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(userId, "Company", "description", "Suwon", "010-0000-0000",
                LocalTime.of(9, 0), LocalTime.of(9, 0));
        PendingCompany savedPendingCompany = pendingCompanyRepository.save(PendingCompany.createPendingCompany(pendingCompanyDto, user));

        return savedPendingCompany.getId();
    }





    private void createAndSavePendingCompanies(){
        for(int i = 0; i < 4; i++){
            UserDto userDto = new UserDto("username" + (i + 1), "password@", "user",
                    "user" + (i + 1) + "@gmail.com", "010-0000-" + 1000 + (i + 1), UserRole.ROLE_USER);
            User user = userRepository.save(User.createUser(userDto));
            PendingCompanyDto pendingCompanyDto = new PendingCompanyDto("Company" + (i + 1), "description", "Suwon", "010-0000-0000",
                    LocalTime.of(9, 0), LocalTime.of(9, 0));
            PendingCompany pendingCompany = PendingCompany.createPendingCompany(pendingCompanyDto, user);
            if(i == 0){
                pendingCompanyId = pendingCompanyRepository.save(pendingCompany).getId();
                userId = user.getId();
            }
            else{
                pendingCompanyRepository.save(pendingCompany);
            }
        }

    }


}