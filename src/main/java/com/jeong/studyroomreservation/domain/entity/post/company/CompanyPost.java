package com.jeong.studyroomreservation.domain.entity.post.company;

import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.CompanyPostFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompanyPost extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "companyPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanyPostFile> companyPostFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;

    private String content;

    private CompanyPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static CompanyPost createCompanyPost(CompanyPostDto dto, Company company){
        CompanyPost companyPost = new CompanyPost(dto.getTitle(), dto.getContent());
        companyPost.setCompany(company);
        return companyPost;
    }

    private void setCompany(Company company){
        this.company = company;
    }

    public void updateCompanyPost(CompanyPostDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}
