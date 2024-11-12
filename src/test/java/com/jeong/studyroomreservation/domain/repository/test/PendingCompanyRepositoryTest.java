package com.jeong.studyroomreservation.domain.repository.test;

import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.PendingCompanyRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(com.jeong.studyroomreservation.UserAuditorAware.class)
class PendingCompanyRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PendingCompanyRepository pendingCompanyRepository;

    @Test
    @Transactional
    @DisplayName("Id로 PendingCompany 조회, Join fetch")
    public void findByIdWithUser(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));

        PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(user.getId(), "Company1",
                "description", "Suwon",
                "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        PendingCompany pendingCompany = pendingCompanyRepository.save(PendingCompany.createPendingCompany(pendingCompanyDto, user));

        //when
        Optional<PendingCompany> optionalPendingCompany =
                pendingCompanyRepository.findByIdWithUser(pendingCompany.getId());
        PendingCompany foundPendingCompany = optionalPendingCompany.get();

        //then
        assertThat(foundPendingCompany.getId()).isEqualTo(pendingCompany.getId());
        assertThat(foundPendingCompany.getUser().getId()).isEqualTo(user.getId());
    }


}