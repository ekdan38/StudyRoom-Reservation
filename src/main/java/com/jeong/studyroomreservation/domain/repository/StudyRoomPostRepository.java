package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomPostRepository extends JpaRepository<StudyRoomPost, Long> {

    Page<StudyRoomPost> findAllByStudyRoomId(Long studyRoomId, Pageable pageable);

    Optional<StudyRoomPost> findByStudyRoomIdAndId(Long studyRoomId, Long id);

}
