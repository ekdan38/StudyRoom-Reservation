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

import static com.jeong.studyroomreservation.web.TestConst.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

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
    @DisplayName("Access Denied_인가 되지 않은 url 접근")
    public void DeniedHandler() throws Exception {
        //given
        ResultActions perform = mockMvc.perform(post("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("access", successAccessToken)
                .cookie(successRefreshToken));
        //when && then
        perform.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message").value("Access denied"))
                .andExpect(jsonPath("data.errorMessage").value("No authority"))
                .andExpect(jsonPath("data.path").exists());
    }

    @Test
    @DisplayName("인증 되지 않은 상태로 인증이 필요한 자원 접근")
    public void EntryPoint() throws Exception {
        //given
        ResultActions perform = mockMvc.perform(post("/api/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //when && then
        perform.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Access denied"))
                .andExpect(jsonPath("data.errorMessage").value("Require authentication"))
                .andExpect(jsonPath("data.path").exists());
    }

}
