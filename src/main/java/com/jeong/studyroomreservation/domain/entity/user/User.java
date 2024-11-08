package com.jeong.studyroomreservation.domain.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
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

    private User(String username, String password, String name, String email, String phoneNumber, UserRole role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }


    //==생성 메서드==//
    public static User createUser(UserDto dto){
        return new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getRole());
    }

    static User dtoToEntity(UserDto dto){
        return new User(
                dto.getId(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getRole());
    }

    public void updateUserRole(UserRole userRole){
        this.role = userRole;
    }
}
