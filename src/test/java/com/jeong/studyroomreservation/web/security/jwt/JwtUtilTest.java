package com.jeong.studyroomreservation.web.security.jwt;

import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("AccessToken 생성")
    public void createJwt_AccessToken(){
        //given && when
        String accessToken = jwtUtil.createJwt("access", "jwtUser", "ROLE_USER", 60000L);

        //then
        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("RefreshToken 생성")
    public void createJwt_RefreshToken(){
        //given && when
        String refreshToken = jwtUtil.createJwt("refresh", "jwtUser", "ROLE_USER", 60000L);

        //then
        assertThat(refreshToken).isNotNull();
    }


    @Test
    @DisplayName("토큰에서 Username 가져오기.")
    public void getUsername(){
        //given
        String username = "jwtUser";
        String accessToken = jwtUtil.createJwt("access", username, "ROLE_USER", 60000L);

        //when
        String foundUsername = jwtUtil.getUsername(accessToken);

        //then
        assertThat(foundUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("토큰에서 Role 가져오기.")
    public void getRole(){
        //given
        UserRole role = UserRole.ROLE_USER;

        String accessToken = jwtUtil.createJwt("access", "jwtUser", role.name(), 60000L);

        //when
        String foundRole = jwtUtil.getRole(accessToken);

        //then
        assertThat(foundRole).isEqualTo(role.name());
    }

    @Test
    @DisplayName("토큰에서 category 가져오기.")
    public void getCategory(){
        //given
        String accessToken = jwtUtil.createJwt("access", "jwtUser", "ROLE_USER", 60000L);
        String refreshToken = jwtUtil.createJwt("refresh", "jwtUser", "ROLE_USER", 60000L);

        //when
        String categoryAccess = jwtUtil.getCategory(accessToken);
        String categoryRefresh = jwtUtil.getCategory(refreshToken);

        //then
        assertThat(categoryAccess).isEqualTo("access");
        assertThat(categoryRefresh).isEqualTo("refresh");
    }

    @Test
    @DisplayName("토큰에 유효 검증.")
    public void isExpired() throws InterruptedException {
        //given
        String accessToken = jwtUtil.createJwt("access", "jwtUser", "ROLE_USER", 2000L);
        String refreshToken = jwtUtil.createJwt("refresh", "jwtUser", "ROLE_USER", 10000000L);
        TimeUnit.MILLISECONDS.sleep(3000L);

        //when
        Boolean expired2 = jwtUtil.isExpired(refreshToken);
        assertThatThrownBy(() -> jwtUtil.isExpired(accessToken)).isInstanceOf(ExpiredJwtException.class);

        //then
        assertThat(expired2).isFalse();
    }

}