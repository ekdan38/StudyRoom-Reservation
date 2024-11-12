package com.jeong.studyroomreservation.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.RefreshRepository;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.security.dto.LoginDto;
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

import static com.jeong.studyroomreservation.web.TestConst.getUniqueEmail;
import static com.jeong.studyroomreservation.web.TestConst.getUniqueUsername;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    RefreshRepository refreshRepository;

    @Autowired
    ObjectMapper objectMapper;

    private String sharedUsername = getUniqueUsername();

    private ResultActions getResultActions(String url, Object responseBody) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseBody)));
    }

    @Transactional
    @BeforeEach
    void init() throws Exception {
        UserDto userDto = new UserDto(
                sharedUsername,
                "testpassword@",
                "testName",
                "test100@gmll.com",
                "010-9999-9998",
                UserRole.ROLE_USER);
        userService.signup(userDto);
    }

    //로그인 테스트
    @Test
    @Transactional
    @DisplayName("로그인 성공")
    public void authentication_Success() throws Exception {
        //given
        String refresh = "";
        String username = sharedUsername;
        String password = "testpassword@";
        LoginDto loginDto = new LoginDto(username, password);

        //when
        ResultActions perform = getResultActions("/api/login", loginDto);

        //then
        MvcResult mvcResult = perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Authentication Success"))
                .andExpect(jsonPath("data.token").value("Token issuance Completed"))
                .andExpect(header().exists("access"))
                .andExpect(cookie().exists("refresh"))
                .andReturn();

        Cookie[] cookies = mvcResult.getResponse().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        Boolean isRefresh = refreshRepository.existsByRefresh(refresh);
        assertThat(isRefresh).isTrue();
    }


    @Test
    @Transactional
    @DisplayName("로그인 실패_일치하는 username 없음")
    public void authentication_Fail_UsernameNotFoundException() throws Exception {
        //given
        String username = getUniqueUsername();
        String password = "testpassword@";
        LoginDto loginDto = new LoginDto(username, password);

        //when
        ResultActions perform = getResultActions("/api/login", loginDto);

        //then
        perform.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Authentication Failed"))
                .andExpect(jsonPath("data.errorMessage").value("Can't find by Username : " + username));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패_일치하는 password 없음")
    public void authentication_Fail_BadCredentialsException() throws Exception {
        //given
        String username = sharedUsername;
        String password = "failpassword";
        LoginDto loginDto = new LoginDto(username, password);

        //when
        ResultActions perform = getResultActions("/api/login", loginDto);

        //then
        perform.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Authentication Failed"))
                .andExpect(jsonPath("data.errorMessage").value("Invalid password"));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패_요청값이 틀림(필드)")
    public void authentication_Fail_UnrecognizedPropertyException() throws Exception {
        //given
        String username = sharedUsername;
        String password = "failpassword";
        LoginDto loginDto = new LoginDto(username, password);

        //when
        ResultActions perform = getResultActions("/api/login", loginDto);

        //then
        perform.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Authentication Failed"))
                .andExpect(jsonPath("data.errorMessage").value("Invalid password"));
    }
    //로그인 테스트 마지막

}
