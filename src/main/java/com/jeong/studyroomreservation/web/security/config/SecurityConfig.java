package com.jeong.studyroomreservation.web.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeong.studyroomreservation.domain.repository.RefreshRepository;
import com.jeong.studyroomreservation.web.security.AuthenticationFilter.RestAuthenticationFilter;
import com.jeong.studyroomreservation.web.security.entrypoint.RestAuthenticationEntryPoint;
import com.jeong.studyroomreservation.web.security.handler.RestAuthenticationDeniedHandler;
import com.jeong.studyroomreservation.web.security.handler.RestAuthenticationFailureHandler;
import com.jeong.studyroomreservation.web.security.handler.RestAuthenticationSuccessHandler;
import com.jeong.studyroomreservation.web.security.jwt.JwtFilter;
import com.jeong.studyroomreservation.web.security.jwt.JwtLogoutFilter;
import com.jeong.studyroomreservation.web.security.jwt.JwtUtil;
import com.jeong.studyroomreservation.web.security.provider.RestAuthenticationProvider;
import com.jeong.studyroomreservation.web.security.userdetails.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;
    private final RestAuthenticationProvider restAuthenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        builder.authenticationProvider(restAuthenticationProvider);
//        AuthenticationManager authenticationManager = builder.build();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll()
                        .requestMatchers("/api/signup", "api/login","/api/logout","/api/reissue", "/error").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/pending-companies", "POST")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/pending-companies", "GET")).hasRole("SYSTEM_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/pending-companies/{id}", "PUT")).hasRole("USER")

                        .requestMatchers("/api/pending-companies/**").hasRole("SYSTEM_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/companies", "GET")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/companies/{id}", "GET")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/companies/**", "GET")).hasRole("SYSTEM_ADMIN")

                        .requestMatchers(new AntPathRequestMatcher("/api/studyrooms", "POST")).hasRole("STUDYROOM_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/studyrooms", "GET")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/studyrooms/{id}", "GET")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/api/studyrooms/{id}", "PUT")).hasRole("STUDYROOM_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/studyrooms/{id}", "DELETE")).hasRole("STUDYROOM_ADMIN")
                        .requestMatchers("/api/test").hasRole("STUDYROOM_ADMIN")
                        .requestMatchers("/api/test1").hasRole("STUDYROOM_ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(except -> except
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new RestAuthenticationDeniedHandler(objectMapper)))
                //"/api/logout" , "POST"
                .addFilterBefore(new JwtLogoutFilter(jwtUtil, refreshRepository, objectMapper), LogoutFilter.class)
                // 매 요청마다.
                .addFilterBefore(new JwtFilter(jwtUtil, customUserDetailsService, objectMapper), UsernamePasswordAuthenticationFilter.class)

                ;
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(restAuthenticationProvider);
        AuthenticationManager authenticationManager = builder.build();

        http
                .securityMatcher("/api/login/**")
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .anyRequest().authenticated()

                )
                .addFilterBefore(restAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(except -> except
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new RestAuthenticationDeniedHandler(objectMapper)))
                ;
        return http.build();
    }


    private RestAuthenticationFilter restAuthenticationFilter(AuthenticationManager authenticationManager){
        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter();
        restAuthenticationFilter.setAuthenticationManager(authenticationManager);
        restAuthenticationFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        restAuthenticationFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        return restAuthenticationFilter;
    }
}
