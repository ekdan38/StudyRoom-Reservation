package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(com.jeong.studyroomreservation.UserAuditorAware.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private String username = "username";
    private String name = "name";
    private String email = "user@gmail.com";
    private String phoneNumber = "010-0000-0000";

    /**
     *
     *     boolean existsByPhoneNumber(String phoneNumber);
     */

    @Test
    @Transactional
    @DisplayName("username으로 User가 존재하는지 유무 조회")
    public void existsByUsername(){
        //given
        createAndSave();

        //when
        boolean exists = userRepository.existsByUsername(username);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("username으로 User 조회")
    public void findByUsername(){
        //given
        createAndSave();

        //when
        Optional<User> optionalUser = userRepository.findByUsername(username);

        //then
        assertThat(optionalUser).isPresent();
    }

    @Test
    @Transactional
    @DisplayName("email으로 User 존재하는지 유무 조회")
    public void existsByEmail(){
        //given
        createAndSave();

        //when
        boolean exists = userRepository.existsByEmail(email);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("phoneNumber로 User 존재하는지 유무 조회")
    public void existsByPhoneNumber(){
        //given
        createAndSave();

        //when
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);

        //then
        assertThat(exists).isTrue();
    }


    private void createAndSave(){
        UserDto userDto = new UserDto(username, "password@", name, email, phoneNumber, UserRole.ROLE_USER);
        User user = User.createUser(userDto);
        userRepository.save(user);
    }

}