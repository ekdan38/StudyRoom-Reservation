package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long> {

    Page<CompanyPost> findAllByCompanyId(Long companyId, Pageable pageable);
    Optional<CompanyPost> findByCompanyIdAndId(Long companyId, Long id);
}
