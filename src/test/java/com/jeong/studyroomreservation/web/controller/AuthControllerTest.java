package com.jeong.studyroomreservation.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.mapper.UserMapper;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;



    @Test
    @DisplayName("회원가입 정상 성공")
    @Transactional
    public void signup_Success() throws Exception {
        //given
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "testId",
                "testpassword@",
                "testName",
                "test@gmail.com",
                "010-6261-2548",
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
        SignupRequestDto signupRequestDto1 = new SignupRequestDto(
                "testId",
                "testpwd@",
                "test",
                "test@gmail.com",
                "010-0000-0000",
                false,
                false);
        UserDto userDto = userMapper.SingnupRequestDtoToUserDto(signupRequestDto1);
        userService.signup(userDto);

        SignupRequestDto signupRequestDto2 = new SignupRequestDto(
                "testId",
                "testpwd@",
                "test",
                "test2@gmail.com",
                "010-0000-0000",
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
                .andExpect(jsonPath("message").value(ErrorCode.LOGINID_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("code").value(ErrorCode.LOGINID_ALREADY_EXISTS.getCode()));

    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패_email이 이미 존재함")
    public void sigunup_fail_exists_email() throws Exception {
        //given
        SignupRequestDto signupRequestDto1 = new SignupRequestDto(
                "testId",
                "testpwd@",
                "test",
                "test@gmail.com",
                "010-0000-0000",
                false,
                false);
        UserDto userDto = userMapper.SingnupRequestDtoToUserDto(signupRequestDto1);
        userService.signup(userDto);

        SignupRequestDto signupRequestDto2 = new SignupRequestDto(
                "testId2",
                "testpwd@",
                "test",
                "test@gmail.com",
                "010-0000-0000",
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
                "test@gmail.com",
                "010-0000-0000",
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
                "testId",
                password,
                "test",
                "test@gmail.com",
                "010-0000-0000",
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
                "testId",
                "testpassword@",
                name,
                "test@gmail.com",
                "010-0000-0000",
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
                "testId",
                "testpassword@",
                "test",
                email,
                "010-0000-0000",
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
    public void signup_fail_invalid_phoneNumberField(String email) throws Exception {
        //given
        SignupRequestDto requestDto = new SignupRequestDto(
                "testId",
                "testpassword@",
                "test",
                email,
                "010-0000-0000",
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