package com.jeong.studyroomreservation.domain.entity;

import com.jeong.studyroomreservation.domain.dto.UserDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;
    /**
     * role은 계층구조 적용할거다.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private User(String loginId, String password, String name, String email, String phoneNumber, UserRole role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public static User createUser(UserDto dto){
        return new User(dto.getLoginId(), dto.getPassword(), dto.getName(), dto.getEmail(), dto.getPhoneNumber(), dto.getRole());
    }
}
