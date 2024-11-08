package com.jeong.studyroomreservation.domain.entity.file;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomFile  extends File{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id")
    private StudyRoom studyRoom;

    public StudyRoomFile(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        super(originalFileName, s3FileName, fileSize, fileType);
    }

    public static StudyRoomFile createStudyRoomFile(FileDto dto, StudyRoom studyRoom){
        StudyRoomFile studyRoomFile =
                new StudyRoomFile(dto.getOriginalFileName(), dto.getS3FileName(), dto.getFileSize(), dto.getFileType());

        studyRoomFile.setStudyRoom(studyRoom);
        return studyRoomFile;
    }
    private void setStudyRoom(StudyRoom studyRoom){
        this.studyRoom = studyRoom;
        studyRoom.getStudyRoomFiles().add(this);
    }
}
