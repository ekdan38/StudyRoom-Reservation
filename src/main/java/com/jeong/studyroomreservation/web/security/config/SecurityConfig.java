package com.jeong.studyroomreservation.web.security.config;

import com.jeong.studyroomreservation.web.security.JwtAuthenticationFilter;
import com.jeong.studyroomreservation.web.security.JwtAuthenticationProvider;
import com.jeong.studyroomreservation.web.security.JwtAuthenticationSuccessHandler;
import com.jeong.studyroomreservation.web.security.JwtDeniedHandler;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Security;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtDeniedHandler jwtDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup").permitAll()
                        .anyRequest().authenticated())
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
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/login/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(http, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(jwtDeniedHandler))
        ;

        return http.build();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(HttpSecurity http, AuthenticationManager authenticationManager){
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
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
