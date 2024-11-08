package com.jeong.studyroomreservation.web.controller;

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
public class ReissueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;


    private String username = getUniqueUsername();
    private String password = "testpassword@";
    private String successAccessToken = "";
    private Cookie successRefreshToken;

    @Transactional
    @BeforeEach
    void init() throws Exception {
        UserDto userDto = new UserDto(
                username,
                password,
                "testName",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                UserRole.ROLE_USER);
        userService.signup(userDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginDto(username, password)))).andReturn();
        successAccessToken = mvcResult.getResponse().getHeader("access");
        successRefreshToken = mvcResult.getResponse().getCookie("refresh");
    }

    @Test
    @Transactional
    @DisplayName("토큰 재발급 성공(access, refresh)")
    public void Reissue_Success() throws Exception {
        //given
        ResultActions perform = getResultActions(successRefreshToken);

        //when
        MvcResult mvcResult = perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Reissue Success"))
                .andExpect(jsonPath("data.token").value("Access and Refresh token reissue Completed"))
                .andReturn();

        //then
        String access = mvcResult.getResponse().getHeader("access");
        Cookie refresh = mvcResult.getResponse().getCookie("refresh");
        assertThat(access).isNotNull();
        assertThat(refresh.getValue()).isNotNull();
    }



    @Test
    @Transactional
    @DisplayName("토큰 재발급 실패_RefreshToken이 null")
    public void Reissue_Fail_RefreshToken_Null() throws Exception {
        //given
        Cookie refresh = new Cookie("refresh", null);
        ResultActions perform = getResultActions(refresh);


        //when && then
        expectFail(perform, "Refresh token is null");
    }


    @Test
    @Transactional
    @DisplayName("토큰 재발급 실패_Expired RefreshToken")
    public void Reissue_Fail_Expired_RefreshToken() throws Exception {
        //given
        String refreshValue = jwtUtil.createJwt("refresh", username, UserRole.ROLE_USER.name(), 100L);
        TimeUnit.MILLISECONDS.sleep(110L);
        Cookie refresh = new Cookie("refresh", refreshValue);

        //when
        ResultActions perform = getResultActions(refresh);

        //then
        expectFail(perform, "Refresh token expired");
    }

    @Test
    @Transactional
    @DisplayName("토큰 재발급 실패_RefreshToken 이 아님")
    public void Reissue_Fail_NotRefreshToken() throws Exception {
        //given
        String refreshValue = jwtUtil.createJwt("access", username, UserRole.ROLE_USER.name(), 600000L);
        Cookie refresh = new Cookie("refresh", refreshValue);

        //when
        ResultActions perform = getResultActions(refresh);

        //then
        expectFail(perform, "Token is not refresh token");
    }

    @Test
    @Transactional
    @DisplayName("토큰 재발급 실패_DB에 존재하지 않음")
    public void Reissue_Fail_Not_Exist_DB() throws Exception {
        //given
        String refreshValue = jwtUtil.createJwt("refresh", username, UserRole.ROLE_USER.name(), 600000L);
        Cookie refresh = new Cookie("refresh", refreshValue);

        //when
        ResultActions perform = getResultActions(refresh);

        //then
        expectFail(perform, "Not exist in DB");
    }

    private ResultActions getResultActions(Cookie refreshToken) throws Exception {
        ResultActions perform = mockMvc.perform(post("/api/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(refreshToken));
        return perform;
    }


    private void expectFail(ResultActions perform, String errorMessage) throws Exception {
        perform.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Invalid refresh token"))
                .andExpect(jsonPath("data.errorMessage").value(errorMessage));
    }

}
