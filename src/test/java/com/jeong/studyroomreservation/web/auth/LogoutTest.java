package com.jeong.studyroomreservation.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.security.dto.LoginDto;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.jeong.studyroomreservation.web.TestConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutTest {

//    @MockBean
//    Clock clock;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private Cookie refreshToken;
    private String username = getUniqueUsername();
    private String password = "testpassword@";

    @Transactional
    @BeforeEach
    void init() throws Exception {
        UserDto userDto = new UserDto(
                username,
                password,
                "testName",
                "Email@gmail.com",
                getUniquePhoneNumber(),
                UserRole.ROLE_USER);
        userService.signup(userDto);
        LoginDto loginDto = new LoginDto(username, password);
        refreshToken = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andReturn().getResponse().getCookie("refresh");

    }

    @Test
    @DisplayName("로그아웃 성공")
    @Transactional
    public void logout_Success() throws Exception {
        //given
        ResultActions perform = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(refreshToken)
        );

        //when && then
        MvcResult mvcResult = perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Logout Success"))
                .andReturn();

        String refreshToken = mvcResult.getResponse().getCookie("refresh").getValue();
        assertThat(refreshToken).isNull();
    }

    @Test
    @DisplayName("로그아웃 실패_refreshToken == null")
    @Transactional
    public void logout_Fail_RefreshTokenisNull() throws Exception {
        //given
        Cookie refreshToken = new Cookie("refresh", null);


        //when
        ResultActions perform = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(refreshToken)
        );

        // then
        perform.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Refresh Token is null"));
    }

    @Test
    @DisplayName("로그아웃 실패_refreshToken 만료됨")
    @Transactional
    public void logout_Fail_RefreshTokenExpired() throws Exception {
        //given
        String newRefresh = jwtUtil.createJwt("refresh", username, "ROLE_USER", 2000L);
        Cookie newRefreshToken = new Cookie("refresh", newRefresh);

        TimeUnit.SECONDS.sleep(3);

        //when
        ResultActions perform = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(newRefreshToken)
        );

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid refresh token"));
    }

    @Test
    @DisplayName("로그아웃 실패_refreshToken이 아님.")
    @Transactional
    public void logout_Fail_Invalid_RefreshToken_notRefresh() throws Exception {
        //given
        String refresh = jwtUtil.createJwt("access", username, UserRole.ROLE_USER.name(), 100000L);
        Cookie refreshToken = new Cookie("refresh", refresh);

        //when
        ResultActions perform = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(refreshToken)
        );

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid refresh token"));
    }

    @Test
    @DisplayName("로그아웃 실패_refreshToken이 DB에 존재하지 않음.")
    @Transactional
    public void logout_Fail_Invalid_RefreshToken_Not_DB() throws Exception {
        //given
        String refresh = jwtUtil.createJwt("refresh", username, UserRole.ROLE_USER.name(), 100000L);
        Cookie refreshToken = new Cookie("refresh", refresh);

        //when
        ResultActions perform = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(refreshToken)
        );

        //then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid refresh token"));
    }




}
