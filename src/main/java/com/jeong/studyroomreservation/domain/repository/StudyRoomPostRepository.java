package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRoomPostRepository extends JpaRepository<StudyRoomPost, Long> {

    Page<StudyRoomPost> findAllByStudyRoomId(Long studyRoomId, Pageable pageable);

    @Query("SELECT srp FROM StudyRoomPost srp LEFT JOIN FETCH srp.studyRoomPostFiles " +
            "WHERE srp.studyRoom.id = :studyRoomId AND srp.id = :id")
    Optional<StudyRoomPost> findByStudyRoomIdAndIdWithStudyRoomPostFiles(@Param("studyRoomId") Long studyRoomId,
                                                                         @Param("id") Long id);

}
