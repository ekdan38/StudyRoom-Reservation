package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.*;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import com.jeong.studyroomreservation.domain.entity.review.Review;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.FileUnsupportedEntityException;
import com.jeong.studyroomreservation.domain.repository.FileRepository;
import com.jeong.studyroomreservation.domain.s3.S3ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "[FileService]")
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private S3ImageUtil s3ImageUtil;

    public Boolean existsByEntityOriginalNameEntityId(String entityType, Long entityId, String originalName) {
        switch (entityType) {
            case "CompanyFile":
                return fileRepository.existsByCompanyFileAndOriginalFileName(entityId, originalName);
            case "CompanyPostFile":
                return fileRepository.existsByCompanyPostFileAndOriginalFileName(entityId, originalName);
            case "ReviewFile":
                return fileRepository.existsByReviewFileAndOriginalFileName(entityId, originalName);
            case "StudyRoomFile":
                return fileRepository.existsByStudyRoomFileAndOriginalFileName(entityId, originalName);
            case "StudyRoomPostFile":
                return fileRepository.existsByStudyRoomPostFileAndOriginalFileName(entityId, originalName);
            default:
                throw new FileUnsupportedEntityException(ErrorCode.FILE_UNSUPPORTED_ENTITY);
        }
    }


    @Transactional
    public File createAndSave(String entityType, FileDto dto, Object entity){
        if ("CompanyFile".equals(entityType)) {
            Company company = (Company) entity;
            CompanyFile companyFile = CompanyFile.createCompanyFile(dto, company);
            return fileRepository.save(companyFile);

        } else if ("CompanyPostFile".equals(entityType)) {
            CompanyPost companyPost = (CompanyPost) entity;
            CompanyPostFile companyPostFile = CompanyPostFile.createCompanyPostFile(dto, companyPost);
            return fileRepository.save(companyPostFile);

        } else if ("ReviewFile".equals(entityType)) {
            Review review = (Review) entity;
            ReviewFile reviewFile = ReviewFile.createReviewFile(dto, review);
            return fileRepository.save(reviewFile);

        } else if ("StudyRoomFile".equals(entityType)) {
            StudyRoom studyRoom = (StudyRoom) entity;
            StudyRoomFile studyRoomFile = StudyRoomFile.createStudyRoomFile(dto, studyRoom);
            return fileRepository.save(studyRoomFile);

        } else if ("StudyRoomPostFile".equals(entityType)) {
            StudyRoomPost studyRoomPost = (StudyRoomPost) entity;
            StudyRoomPostFile studyRoomPostFile = StudyRoomPostFile.createStudyRoomPostFile(dto, studyRoomPost);
            return fileRepository.save(studyRoomPostFile);

        } else {
            throw new FileUnsupportedEntityException(ErrorCode.FILE_UNSUPPORTED_ENTITY);
        }
    }

    @Transactional
    public void deleteFileByEntityAndS3FileName(String entityType, Long entityId, String s3FileName) {
        int deletedCount;
        switch (entityType) {
            case "CompanyFile":
                deletedCount = fileRepository.deleteByCompanyFileAndS3FileName(entityId, s3FileName);
                break;
            case "CompanyPostFile":
                deletedCount = fileRepository.deleteByCompanyPostFileAndS3FileName(entityId, s3FileName);
                break;
            case "ReviewFile":
                deletedCount = fileRepository.deleteByReviewFileAndS3FileName(entityId, s3FileName);
                break;
            case "StudyRoomFile":
                deletedCount = fileRepository.deleteByStudyRoomFileAndS3FileName(entityId, s3FileName);
                break;
            case "StudyRoomPostFile":
                deletedCount = fileRepository.deleteByStudyRoomPostFileAndS3FileName(entityId, s3FileName);
                break;
            default:
                throw new FileUnsupportedEntityException(ErrorCode.FILE_UNSUPPORTED_ENTITY);
        }

        if (deletedCount == 0) {
            throw new RuntimeException("Delete Fail");
        }
    }

    public List<File> findFilesByEntityTypeAndEntityId(String entityType, Long entityId) {
        switch (entityType) {
            case "CompanyFile":
                return fileRepository.findCompanyFilesByCompanyId(entityId);
            case "CompanyPostFile":
                return fileRepository.findCompanyPostFilesByCompanyPostId(entityId);
            case "ReviewFile":
                return fileRepository.findReviewFilesByReviewId(entityId);
            case "StudyRoomFile":
                return fileRepository.findStudyRoomFilesByStudyRoomId(entityId);
            case "StudyRoomPostFile":
                return fileRepository.findStudyRoomPostFilesByStudyRoomPostId(entityId);
            default:
                throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        }
    }
}
