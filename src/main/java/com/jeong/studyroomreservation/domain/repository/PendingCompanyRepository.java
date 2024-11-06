package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingCompanyRepository extends JpaRepository<PendingCompany, Long> {

    Page<PendingCompany> findAll(Pageable pageable);
    Optional<PendingCompany> findById(Long id);
}
