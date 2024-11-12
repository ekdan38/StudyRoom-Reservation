package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.error.exception.SignupException;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    @Test
    @Transactional
    @DisplayName("회원가입 로직 성공")
    public void signup_success(){
        //given
        User user1 = createAndSaveUser();

        UserDto userDto2 = new UserDto("usernam10", "password@", "user",
                "user1@gmail.com", "010-0000-0001", UserRole.ROLE_USER);

        //when
        UserDto responseDto = userService.signup(userDto2);

        //then
        assertThat(responseDto.getId()).isNotEqualTo(user1.getId());
    }

    @ParameterizedTest
    @CsvSource({
            "username, user1@gmail.com, 010-0000-0001" ,
            "username1, user@gmail.com, 010-0000-0001"
    })
    @Transactional
    @DisplayName("회원가입 로직 실패")
    public void signup_fail(String username, String email, String phoneNumber){
        //given
        createAndSaveUser();

        UserDto userDto2 = new UserDto(username, "password@", "user",
                email, phoneNumber, UserRole.ROLE_USER);

        //when && then
        assertThatThrownBy(() -> userService.signup(userDto2)).isInstanceOf(SignupException.class);
    }
    @Test
    @Transactional
    @DisplayName("user Role 변경")
    public void updateRole(){
        //given
        User user = createAndSaveUser();
        Long userId = user.getId();

        //when
        UserDto responseDto = userService.updateRole(userId, UserRole.ROLE_STUDYROOM_ADMIN);

        //then
        assertThat(responseDto.getRole()).isEqualTo(UserRole.ROLE_STUDYROOM_ADMIN);
    }

    private User createAndSaveUser(){
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0010", UserRole.ROLE_USER);
        return userRepository.save(User.createUser(userDto));
    }






}