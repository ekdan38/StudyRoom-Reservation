package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    Page<StudyRoom> findByCompanyId(Long companyId, Pageable pageable);
}

