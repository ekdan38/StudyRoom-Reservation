package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String LoginId);

    boolean existsByEmail(String email);
}

