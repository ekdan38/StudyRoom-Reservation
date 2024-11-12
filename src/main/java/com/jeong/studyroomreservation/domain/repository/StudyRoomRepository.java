package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    Page<StudyRoom> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT s FROM StudyRoom s LEFT JOIN FETCH s.studyRoomFiles WHERE s.id = :id AND s.company.id = :companyId")
    Optional<StudyRoom> findByCompanyIdAndIdWithStudyRoomFiles(@Param("companyId") Long companyId, @Param("id") Long id);

    @Query("SELECT s FROM StudyRoom s JOIN FETCH s.company WHERE s.id = :id")
    Optional<StudyRoom> findByIdWithCompany(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StudyRoom s JOIN FETCH s.company JOIN FETCH s.company.user WHERE s.id = :id")
    Optional<StudyRoom> findByIdWithCompanyWithUser(@Param("id") Long id);

}

