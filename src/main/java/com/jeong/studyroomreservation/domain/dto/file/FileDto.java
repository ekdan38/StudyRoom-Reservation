package com.jeong.studyroomreservation.domain.dto.file;

import lombok.Data;

@Data
public class FileDto {

    private Long id;

    private String originalFileName;

    private String S3FileName;

    private Long fileSize;

    private String fileType;

    public FileDto(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        this.originalFileName = originalFileName;
        S3FileName = s3FileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
}
