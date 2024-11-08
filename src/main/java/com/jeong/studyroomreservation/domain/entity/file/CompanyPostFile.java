package com.jeong.studyroomreservation.domain.entity.file;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyPostFile extends File{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_post_id")
    private CompanyPost companyPost;


    public CompanyPostFile(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        super(originalFileName, s3FileName, fileSize, fileType);
    }

    public static CompanyPostFile createCompanyPostFile(FileDto dto, CompanyPost companyPost){
        CompanyPostFile companyPostFile =
                new CompanyPostFile(dto.getOriginalFileName(), dto.getS3FileName(), dto.getFileSize(), dto.getFileType());
        companyPostFile.setCompanyPostFile(companyPost);
        return companyPostFile;
    }

    private void setCompanyPostFile(CompanyPost companyPost){
        this.companyPost = companyPost;
        companyPost.getCompanyPostFiles().add(this);
    }
}
