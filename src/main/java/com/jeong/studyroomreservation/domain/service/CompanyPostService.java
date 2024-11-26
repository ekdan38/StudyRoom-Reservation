package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.CompanyPostFile;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPostMapper;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.NotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.S3Exception;
import com.jeong.studyroomreservation.domain.repository.CompanyPostRepository;
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
@Slf4j(topic = "[CompanyPostService]")
@Transactional(readOnly = true)
public class CompanyPostService {

    private static final String ENTITY_TYPE = "CompanyPostFile";
    private final CompanyPostMapper companyPostMapper;
    private final CompanyPostRepository companyPostRepository;
    private final CompanyService companyService;
    private final S3ImageUtil s3ImageUtil;
    private final FileService fileService;

    // 생성, 저장
    @Transactional
    public CompanyPostResponseDto createAndSave(Long companyId, CompanyPostDto dto, List<MultipartFile> files) {
        //company 조회 쿼리 1번
        Company company = companyService.findById(companyId);

        // companyPost 저장 쿼리 1번
        CompanyPost savedCompanyPost = companyPostRepository.save(CompanyPost.createCompanyPost(dto, company));
        CompanyPostResponseDto companyPostResponseDto = companyPostMapper.entityToResponse(savedCompanyPost);

        return saveFiles(files, savedCompanyPost, companyPostResponseDto);
    }

    // 여러건 조회
    public Page<CompanyPostResponseDto> getCompanyPosts(Long companyId, Pageable pageable) {
        Page<CompanyPost> page = companyPostRepository.findAllByCompanyId(companyId, pageable);
        return page.map(p -> {
            CompanyPostResponseDto companyPostResponseDto = companyPostMapper.entityToResponse(p);
            List<CompanyPostFile> companyPostFiles = p.getCompanyPostFiles();
            for (CompanyPostFile companyPostFile : companyPostFiles) {
                companyPostResponseDto.getImages().add(companyPostFile.getS3FileName());
            }
            return companyPostResponseDto;
        });
    }

    //단건 조회 (n + 1해결) join fetch
    public CompanyPostResponseDto getCompanyPost(Long companyId, Long id){
        CompanyPost foundCompanyPost = companyPostRepository.findByCompanyIdAndIdWithCompanyPostFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMPANY_POST_NOT_FOUND));

        CompanyPostResponseDto companyPostResponseDto = companyPostMapper.entityToResponse(foundCompanyPost);
        List<CompanyPostFile> companyPostFiles = foundCompanyPost.getCompanyPostFiles();

        for (CompanyPostFile companyPostFile : companyPostFiles) {
            companyPostResponseDto.getImages().add(companyPostFile.getS3FileName());
        }
        return companyPostResponseDto;
    }


    // 수정
    @Transactional
    public CompanyPostUpdateResponseDto updateCompanyPost(Long companyId,
                                                          Long id,
                                                          CompanyPostDto dto,
                                                          List<MultipartFile> files,
                                                          List<String> deleteFiles){

        CompanyPost foundCompanyPost = companyPostRepository.findByCompanyIdAndIdWithCompanyPostFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMPANY_POST_NOT_FOUND));
        foundCompanyPost.updateCompanyPost(dto);

        CompanyPostResponseDto companyPostResponseDto = companyPostMapper.entityToResponse(foundCompanyPost);
        CompanyPostUpdateResponseDto companyPostUpdateResponseDto =
                companyPostMapper.responseToUpdateResponse(saveFiles(files, foundCompanyPost, companyPostResponseDto));

        if (deleteFiles != null) {
            try {
                for (String deleteImage : deleteFiles) {
                    s3ImageUtil.deleteImageFromS3(deleteImage);
                    fileService.deleteFileByEntityAndS3FileName(ENTITY_TYPE, id, deleteImage);
                    companyPostUpdateResponseDto.getDeleteImages().add(deleteImage);
                }
            } catch (Exception e) {
                throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
            }
        }
        return companyPostUpdateResponseDto;
    }


    // 삭제
    @Transactional
    public void deleteCompanyPost(Long companyId, Long id){
        companyPostRepository.findByCompanyIdAndIdWithCompanyPostFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMPANY_POST_NOT_FOUND));
        List<File> companyPostFiles = fileService.findFilesByEntityTypeAndEntityId(ENTITY_TYPE, id);

        try{
            for (File file : companyPostFiles) {
                s3ImageUtil.deleteImageFromS3(file.getS3FileName());
            }
        } catch (Exception e){
            throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
        }
        companyPostRepository.deleteById(id);
    }

    private CompanyPostResponseDto saveFiles(List<MultipartFile> files, CompanyPost companyPost, CompanyPostResponseDto responseDto){
        String storeFileName = null;
        if(files != null){
            for (MultipartFile file : files) {
                storeFileName = null;
                String originalFilename = file.getOriginalFilename();
                String extention = file.getOriginalFilename()
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

                try{
                    storeFileName = s3ImageUtil.upload(file);

                    FileDto fileDto = new FileDto(originalFilename, storeFileName, file.getSize(), extention);
                    File studyRoomFile = fileService.createAndSave(ENTITY_TYPE, fileDto, companyPost);

                    responseDto.getImages().add(studyRoomFile.getS3FileName());
                } catch (Exception e){
                    s3ImageUtil.deleteImageFromS3(storeFileName);
                    fileService.deleteFileByEntityAndS3FileName(ENTITY_TYPE, companyPost.getId(), storeFileName);
                    throw new S3Exception(ErrorCode.S3_EXCEPTION_SAVE_DB);
                }
            }
        }
        return responseDto;
    }
}
