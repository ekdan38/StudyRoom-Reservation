package com.jeong.studyroomreservation.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.jeong.studyroomreservation.web.TestConst.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtUtil jwtUtil;


    @Test
    @DisplayName("회원가입 정상 성공")
    @Transactional
    public void signup_Success() throws Exception {
        //given
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                getUniqueUsername(),
                "testpassword@",
                "testName",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false
        );

        //when
        ResultActions perform = mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)));

        //then
        perform
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패_loginId 가 이미 존재함")
    public void sigunup_fail_exists_loginId() throws Exception {
        //given
        String username = getUniqueUsername();

        SignupRequestDto signupRequestDto1 = new SignupRequestDto(
                username,
                "testpwd@",
                "test",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false);
        UserDto userDto = userMapper.requestDtoToUserDto(signupRequestDto1);
        userService.signup(userDto);

        SignupRequestDto signupRequestDto2 = new SignupRequestDto(
                username,
                "testpwd@",
                "test",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false);

        //when
        ResultActions perform = mockMvc.perform(post("/api/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto2)));

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("code").value(ErrorCode.USERNAME_ALREADY_EXISTS.getCode()));

    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패_email이 이미 존재함")
    public void sigunup_fail_exists_email() throws Exception {
        //given
        String email = getUniqueEmail();
        SignupRequestDto signupRequestDto1 = new SignupRequestDto(
                getUniqueUsername(),
                "testpwd@",
                "test",
                email,
                getUniquePhoneNumber(),
                false,
                false);
        UserDto userDto = userMapper.requestDtoToUserDto(signupRequestDto1);
        userService.signup(userDto);

        SignupRequestDto signupRequestDto2 = new SignupRequestDto(
                getUniqueUsername(),
                "testpwd@",
                "test",
                email,
                getUniquePhoneNumber(),
                false,
                false);

        //when
        ResultActions perform = mockMvc.perform(post("/api/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto2)));

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("code").value(ErrorCode.EMAIL_ALREADY_EXISTS.getCode()));

    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패_phoneNumber 이미 존재함")
    public void sigunup_fail_exists_phoneNumber() throws Exception {
        //given
        String phoneNumber = getUniquePhoneNumber();
        SignupRequestDto signupRequestDto1 = new SignupRequestDto(
                getUniqueUsername(),
                "testpwd@",
                "test",
                getUniqueEmail(),
                phoneNumber,
                false,
                false);
        UserDto userDto = userMapper.requestDtoToUserDto(signupRequestDto1);
        userService.signup(userDto);

        SignupRequestDto signupRequestDto2 = new SignupRequestDto(
                getUniqueUsername(),
                "testpwd@",
                "test",
                getUniqueEmail(),
                phoneNumber,
                false,
                false);

        //when
        ResultActions perform = mockMvc.perform(post("/api/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto2)));

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("code").value(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS.getCode()));

    }

    @ParameterizedTest
    @CsvSource({
            ",",
            " ,",
            "Id",
            "testUserIdtestUserIdtestUserId",
            "testloginId@"

    })
    @Transactional
    @DisplayName("회원가입 실패_loginIdField 오류")
    public void signup_fail_invalid_loginIdField(String loginId) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                loginId,
                "testpassword@",
                "test",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false);

        //when
        ResultActions resultActions = performSignup(requestDto);

        //then
        expectSignupFieldErrors(resultActions);
    }

    @ParameterizedTest
    @CsvSource({
            ", ",
            " ,",
            "pwd",
            "testPwdtestPwdtestPwd",
            "testPassworld"
    })
    @Transactional
    @DisplayName("회원가입 실패_passwordField 오류")
    public void signup_fail_invalid_passwordField(String password) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                getUniqueUsername(),
                password,
                "test",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false
        );

        //when
        ResultActions resultActions = performSignup(requestDto);

        //then
        expectSignupFieldErrors(resultActions);

    }

    @ParameterizedTest
    @CsvSource({
            ", ",
            " ,",
            "n",
            "testnameistolong",
    })
    @Transactional
    @DisplayName("회원가입 실패_nameField 오류")
    public void signup_fail_invalid_nameField(String name) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                getUniqueUsername(),
                "testpassword@",
                name,
                getUniqueEmail(),
                getUniquePhoneNumber(),
                false,
                false
        );

        //when
        ResultActions resultActions = performSignup(requestDto);

        //then
        expectSignupFieldErrors(resultActions);
    }

    @ParameterizedTest
    @CsvSource({
            ", ",
            " ,",
            "testEmail",
    })
    @Transactional
    @DisplayName("회원가입 실패_emailField 오류")
    public void signup_fail_invalid_emailField(String email) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                getUniqueUsername(),
                "testpassword@",
                "test",
                email,
                getUniquePhoneNumber(),
                false,
                false
        );

        //when
        ResultActions resultActions = performSignup(requestDto);

        //then
        expectSignupFieldErrors(resultActions);
    }

    @ParameterizedTest
    @CsvSource({
            ", ",
            " ,",
            "01000000000",
            "010-0000",
    })
    @Transactional
    @DisplayName("회원가입 실패_phoneNumber 오류")
    public void signup_fail_invalid_phoneNumberField(String phoneNumber) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                getUniqueUsername(),
                "testpassword@",
                "test",
                getUniqueEmail(),
                phoneNumber,
                false,
                false
        );

        //when
        ResultActions resultActions = performSignup(requestDto);

        //then
        expectSignupFieldErrors(resultActions);
    }


private ResultActions performSignup(SignupRequestDto requestDto) throws Exception {
    return mockMvc.perform(post("/api/signup")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));
}

    private void expectSignupFieldErrors(ResultActions perform) throws Exception {
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data.errors[0].field").exists())
                .andExpect(jsonPath("data.errors[0]").exists())
                .andExpect(jsonPath("data.errors[0]").exists())
                .andExpect(jsonPath("data.errors[0]").exists())
        ;
    }
}