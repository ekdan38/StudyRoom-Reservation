package com.jeong.studyroomreservation.domain.entity.file;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyFile extends File{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public CompanyFile(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        super(originalFileName, s3FileName, fileSize, fileType);
    }

    public static CompanyFile createCompanyFile(FileDto dto, Company company){
        CompanyFile companyFile =
                new CompanyFile(dto.getOriginalFileName(), dto.getS3FileName(), dto.getFileSize(), dto.getFileType());
        companyFile.setCompany(company);
        return companyFile;
    }

    private void setCompany(Company company){
        this.company = company;
        company.getCompanyFiles().add(this);

    }
}
