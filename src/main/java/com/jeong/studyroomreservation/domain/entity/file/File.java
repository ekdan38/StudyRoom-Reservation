package com.jeong.studyroomreservation.domain.entity.file;

import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type")
public class File extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    
    private String S3FileName;

    private Long fileSize;

    private String fileType;


    protected File(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        this.originalFileName = originalFileName;
        this.S3FileName = s3FileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
}
