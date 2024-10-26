package com.jeong.studyroomreservation.domain.entity;

import jakarta.persistence.*;

@Entity
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
}
