package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingCompanyRepository extends JpaRepository<PendingCompany, Long> {

}
