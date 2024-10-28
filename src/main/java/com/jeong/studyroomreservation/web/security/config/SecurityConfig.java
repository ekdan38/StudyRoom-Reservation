package com.jeong.studyroomreservation.web.security.config;

import com.jeong.studyroomreservation.web.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtDeniedHandler jwtDeniedHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(jwtAuthenticationProvider);
        AuthenticationManager authenticationManager = builder.build();

        http
                //jwt 사용하기에 세션 생성 xx
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/api/all").hasRole("USER")
                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
        ;

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(jwtAuthenticationProvider);
        AuthenticationManager authenticationManager = builder.build();

        http
                //jwt 사용하기에 세션 생성 xx
                .securityMatcher("/api/login/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/login/all").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtAuthenticationFilter(http, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
        ;

        return http.build();
    }

    private JwtAuthorizationFilter jwtAuthorizationFilter(){
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtils, jwtUserDetailsService);
        return jwtAuthorizationFilter;
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(HttpSecurity http, AuthenticationManager authenticationManager){
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        return jwtAuthenticationFilter;
    }



    @Bean
    public RoleHierarchy hierarchy(){
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER\n" +
                "ROLE_MANAGER > ROLE_USER\n" +
                "ROLE_USER > ROLE_ANONYMOUS");
    }
}
