package com.jeong.studyroomreservation.domain.entity.file;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomPostFile extends File{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_post_id")
    private StudyRoomPost studyRoomPost;

    public StudyRoomPostFile(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        super(originalFileName, s3FileName, fileSize, fileType);
    }

    public static StudyRoomPostFile createStudyRoomPostFile(FileDto dto, StudyRoomPost studyRoomPost){
        StudyRoomPostFile studyRoomPostFile =
                new StudyRoomPostFile(dto.getOriginalFileName(), dto.getS3FileName(), dto.getFileSize(), dto.getFileType());
        studyRoomPostFile.setStudyRoomPost(studyRoomPost);
        return studyRoomPostFile;
    }

    private void setStudyRoomPost(StudyRoomPost studyRoomPost){
        this.setStudyRoomPost(studyRoomPost);
        studyRoomPost.getStudyRoomPostFiles().add(this);
    }

}
