package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    Page<StudyRoom> findAllByCompanyId(Long companyId, Pageable pageable);
    Optional<StudyRoom> findAllByCompanyIdAndId(Long companyId, Long id);
}

