package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.company.CompanyResponseDto;
import com.jeong.studyroomreservation.domain.dto.company.CompanyUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.compnay.CompanyMapper;
import com.jeong.studyroomreservation.domain.entity.file.CompanyFile;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.CompanyNotFoundException;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.s3.S3ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "[CompanyService]")
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final S3ImageUtil s3ImageUtil;
    private final CompanyMapper companyMapper;
    private final FileService fileService;

    // 페이징으로 Company들 조회
    // 조회 쿼리 1번
    public Page<CompanyResponseDto> getCompanies(Pageable pageable) {
        Page<Company> page = companyRepository.findAll(pageable); //조회 쿼리 1 번
        return page.map(p -> {
            CompanyResponseDto companyResponseDto = companyMapper.entityToResponse(p);
                    List<CompanyFile> companyFiles = p.getCompanyFiles();
                    for (CompanyFile companyFile : companyFiles) {
                        companyResponseDto.getImages().add(companyFile.getS3FileName());
                    }
                    return companyResponseDto;
                }

        );
    }

    // Company 단건 조회
    // 조회 쿼리 1번
    public CompanyResponseDto getCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        CompanyResponseDto companyResponseDto = companyMapper.entityToResponse(company);
        List<CompanyFile> companyFiles = company.getCompanyFiles();
        for (CompanyFile companyFile : companyFiles) {
            companyResponseDto.getImages().add(companyFile.getS3FileName());
        }
        return companyResponseDto;
    }

    // Company 업데이트
    // company 조회 쿼리 1번
    // update 쿼리 1번
    @Transactional
    public CompanyUpdateResponseDto updateCompany(Long id, CompanyDto updateDto, List<MultipartFile> files, List<String> deleteFiles) {
        // company 조회 쿼리 1번
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        company.updateCompany(updateDto);

        CompanyUpdateResponseDto companyUpdateResponseDto =
                new CompanyUpdateResponseDto(company.getName(), company.getDescription(), company.getLocation(), company.getPhoneNumber());
        String storeFileName = null;

        if (files != null) {
            for (MultipartFile file : files) {
                storeFileName = null;
                String originalFilename = file.getOriginalFilename();

                // db에서 file의 EntityType이 CompanyFile이고 companyId, originalName이 일치하는 파일이 있나 검색.
                Boolean exists = fileService.existsByEntityOriginalNameEntityId("CompanyFile", id, originalFilename);

                if (!exists) {
                    try {
                        storeFileName = s3ImageUtil.upload(file);

                        String extention = file.getOriginalFilename()
                                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                        FileDto fileDto = new FileDto(file.getOriginalFilename(), storeFileName, file.getSize(), extention);
                        File companyFile = fileService.createAndSave("CompanyFile", fileDto, company);

                        companyUpdateResponseDto.getNewImages().add(companyFile.getS3FileName());
                    } catch (Exception e) {
                        fileService.deleteFileByEntityAndS3FileName("CompanyFile", id, storeFileName);
                        throw new IllegalArgumentException("db에 저장하다가 오류남.");
                    }
                }
            }
        }

        if (deleteFiles != null) {
            try {
                for (String deleteImage : deleteFiles) {
                    s3ImageUtil.deleteImageFromS3(deleteImage);
                    fileService.deleteFileByEntityAndS3FileName("CompanyFile", id, deleteImage);
                    companyUpdateResponseDto.getDeleteImages().add(deleteImage);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제하다가 오류남.");
            }
        }


        return companyUpdateResponseDto;
        // update 쿼리 1번
    }


    // PendingCompany 승인 로직
    @Transactional
    public CompanyDto approvalPendingCompany(PendingCompanyDto pendingCompanyDto, User user) {
        CompanyDto companyDto = new CompanyDto(
                pendingCompanyDto.getName(),
                pendingCompanyDto.getDescription(),
                pendingCompanyDto.getLocation(),
                pendingCompanyDto.getPhoneNumber());

        Company company = Company.createCompany(companyDto, user);

        //Company 저장
        // 저장 쿼리 1번
        Company savedCompany = companyRepository.save(company);
        return companyMapper.entityToDto(savedCompany, null);
    }

    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        List<File> companyFiles = fileService.findFilesByEntityTypeAndEntityId("CompanyFile", id);

        try {
            for (File companyFile : companyFiles) {
                s3ImageUtil.deleteImageFromS3(companyFile.getS3FileName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("지우다가 오류");
        }
        companyRepository.deleteById(id);
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }


}
