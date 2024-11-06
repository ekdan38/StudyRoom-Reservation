package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserId(Long userId);
}
