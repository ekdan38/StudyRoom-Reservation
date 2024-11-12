package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PendingCompanyRepository extends JpaRepository<PendingCompany, Long> {

    @Query("SELECT p FROM PendingCompany p join fetch p.user WHERE p.id = :id")
    Optional<PendingCompany> findByIdWithUser(@Param("id") Long id);
}
