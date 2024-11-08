package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    Page<StudyRoom> findAllByCompanyId(Long companyId, Pageable pageable);

    Optional<StudyRoom> findByCompanyIdAndId(Long companyId, Long id);

    @Query("SELECT s FROM StudyRoom s JOIN FETCH s.company WHERE s.id = :id")
    Optional<StudyRoom> findByIdWithCompany(@Param("id") Long id);


}

