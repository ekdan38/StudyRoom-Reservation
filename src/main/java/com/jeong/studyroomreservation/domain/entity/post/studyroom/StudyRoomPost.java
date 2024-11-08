package com.jeong.studyroomreservation.domain.entity.post.studyroom;

import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.file.StudyRoomPostFile;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyRoomPost extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "studyRoomPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoomPostFile> studyRoomPostFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id")

    private StudyRoom studyRoom;
    private String title;
    private String content;

    private StudyRoomPost(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public static StudyRoomPost createStudyRoomPost(StudyRoomPostDto dto, StudyRoom studyRoom){
        StudyRoomPost studyRoomPost = new StudyRoomPost(dto.getTitle(), dto.getContent());
        studyRoomPost.setStudyRoom(studyRoom);
        return studyRoomPost;
    }

    private void setStudyRoom(StudyRoom studyRoom){
        this.studyRoom = studyRoom;
    }

    public void updateStudyRoomPost(StudyRoomPostDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}
