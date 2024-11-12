package com.jeong.studyroomreservation.web.controller.auth;

import com.jeong.studyroomreservation.domain.entity.refresh.Refresh;
import com.jeong.studyroomreservation.domain.repository.RefreshRepository;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0){
            ResponseDto<Object> responseBody =
                    getBadRequestResponseBody("Invalid refresh token", "Refresh token is null");
            return ResponseEntity.badRequest().body(responseBody);
        }
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        //토큰이 null
        if (refresh == null) {

            ResponseDto<Object> responseBody =
                    getBadRequestResponseBody("Invalid refresh token", "Refresh token is null");
            return ResponseEntity.badRequest().body(responseBody);
        }

        String category= null;
        try{
            category = jwtUtil.getCategory(refresh);
        } catch (Exception e){
            ResponseDto<Object> responseBody
                    = getBadRequestResponseBody("Invalid refresh token", "Token is not refresh token");
            return ResponseEntity.badRequest().body(responseBody);
        }


        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        if (!category.equals("refresh")) {

            ResponseDto<Object> responseBody
                    = getBadRequestResponseBody("Invalid refresh token", "Token is not refresh token");
            return ResponseEntity.badRequest().body(responseBody);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            ResponseDto<Object> responseBody
                    = getBadRequestResponseBody("Invalid refresh token", "Not exist in DB");
            return ResponseEntity.badRequest().body(responseBody);
        }


        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (JwtException e) {

            ResponseDto<Object> responseBody
                    = getBadRequestResponseBody("Invalid refresh token", "Refresh token expired");
            return ResponseEntity.badRequest().body(responseBody);
        }


        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        HashMap<String, String> data = new LinkedHashMap<>();
        data.put("token", "Access and Refresh token reissue Completed");
        ResponseDto<Object> responseBody = new ResponseDto<>("Reissue Success", data);
        return ResponseEntity.ok().body(responseBody);
    }

    private ResponseDto<Object> getBadRequestResponseBody(String message, String errorMessage) {
        HashMap<String, String> data = new LinkedHashMap<>();
        data.put("errorMessage", errorMessage);
        ResponseDto<Object> responseBody = new ResponseDto<>(message, data);
        return responseBody;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);
        Refresh refreshEntity = Refresh.createRefresh(username, refresh, date.toString());
        refreshRepository.save(refreshEntity);
    }
}
