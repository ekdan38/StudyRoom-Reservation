package com.jeong.studyroomreservation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.security.dto.LoginDto;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterTest {

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

    @Transactional
    @BeforeEach
    void init() throws Exception {
        UserDto userDto = new UserDto(
                username,
                password,
                "testName",
                getUniqueEmail(),
                getUniquePhoneNumber(),
                UserRole.ROLE_STUDYROOM_ADMIN);
        userService.signup(userDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginDto(username, password)))).andReturn();
        successAccessToken = mvcResult.getResponse().getHeader("access");
    }

    @Test
    @Transactional
    @DisplayName("JwtFilter 인가 성공")
    public void jwtFilter_Success() throws Exception{
        //given && when
        ResultActions perform = mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", successAccessToken));

        //then
        perform
                .andDo(print())
                .andExpect(jsonPath("data.isAuthenticated").value(true));
    }


    @Test
    @Transactional
    @DisplayName("JwtFilter 인가 실패_AccessToken 만료")
    public void jwtFilter_Fail_ExpiredAccessToken() throws Exception{
        //given
        String accessValue = jwtUtil.createJwt("access", username, UserRole.ROLE_STUDYROOM_ADMIN.name(), 100L);
        TimeUnit.MILLISECONDS.sleep(110L);

        //when
        ResultActions perform = mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", accessValue));

        //then
        perform
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Invalid access token"))
                .andExpect(jsonPath("data.errorMessage").value("Access token expired"));
    }

    @Test
    @Transactional
    @DisplayName("JwtFilter 인가 실패_AccessToken 이 아님")
    public void jwtFilter_Fail_NotAccessToken() throws Exception{
        //given
        String accessValue = jwtUtil.createJwt("refresh", username, UserRole.ROLE_STUDYROOM_ADMIN.name(), 600000L);

        //when
        ResultActions perform = mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", accessValue));

        //then
        perform
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Invalid access token"))
                .andExpect(jsonPath("data.errorMessage").value("Token is not access token"));
    }

    @Test
    @Transactional
    @DisplayName("JwtFilter 인가 실패_AccessToken의 Role이 위조됨")
    public void jwtFilter_Fail_InvalidRole() throws Exception{
        //given
        String accessValue = jwtUtil.createJwt("access", username, UserRole.ROLE_USER.name(), 600000L);

        //when
        ResultActions perform = mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", accessValue));

        //then
        perform
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Invalid access token"))
                .andExpect(jsonPath("data.errorMessage").value("This access token has wrong role"));
    }

    @Test
    @Transactional
    @DisplayName("JwtFilter 인가 실패_AccessToken의 Username이 위조됨")
    public void jwtFilter_Fail_InvalidUsername() throws Exception{
        //given
        String accessValue = jwtUtil.createJwt("access", "fakeUsername", UserRole.ROLE_STUDYROOM_ADMIN.name(), 600000L);

        //when
        ResultActions perform = mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", accessValue));

        //then
        perform
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Invalid access token"))
                .andExpect(jsonPath("data.errorMessage").exists());
    }


}
