package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long> {

    Page<CompanyPost> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT cp FROM CompanyPost cp LEFT JOIN FETCH cp.companyPostFiles WHERE cp.company.id = :companyId AND cp.id = :id")
    Optional<CompanyPost> findByCompanyIdAndIdWithCompanyPostFiles(@Param("companyId") Long companyId, @Param("id") Long id);
}
