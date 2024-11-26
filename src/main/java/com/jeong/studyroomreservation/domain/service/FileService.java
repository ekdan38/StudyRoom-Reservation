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
import com.jeong.studyroomreservation.domain.error.exception.S3Exception;
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
    private final static String COMPANY_FILE = "CompanyFile";
    private final static String COMPANY_POST_FILE = "CompanyPostFile";
    private final static String REVIEW_FILE = "ReviewFile";
    private final static String STUDYROOM_FILE = "StudyRoomFile";
    private final static String STUDYROOM_POST_FILE = "StudyRoomPostFile";

    private final FileRepository fileRepository;
    private S3ImageUtil s3ImageUtil;

    public Boolean existsByEntityOriginalNameEntityId(String entityType, Long entityId, String originalName) {
        switch (entityType) {
            case COMPANY_FILE:
                return fileRepository.existsByCompanyFileAndOriginalFileName(entityId, originalName);
            case COMPANY_POST_FILE:
                return fileRepository.existsByCompanyPostFileAndOriginalFileName(entityId, originalName);
            case REVIEW_FILE:
                return fileRepository.existsByReviewFileAndOriginalFileName(entityId, originalName);
            case STUDYROOM_FILE:
                return fileRepository.existsByStudyRoomFileAndOriginalFileName(entityId, originalName);
            case STUDYROOM_POST_FILE:
                return fileRepository.existsByStudyRoomPostFileAndOriginalFileName(entityId, originalName);
            default:
                throw new S3Exception(ErrorCode.S3_EXCEPTION_UNSUPPORTED_TYPE);
        }
    }


    @Transactional
    public File createAndSave(String entityType, FileDto dto, Object entity){
        if (COMPANY_FILE.equals(entityType)) {
            Company company = (Company) entity;
            CompanyFile companyFile = CompanyFile.createCompanyFile(dto, company);
            return fileRepository.save(companyFile);

        } else if (COMPANY_POST_FILE.equals(entityType)) {
            CompanyPost companyPost = (CompanyPost) entity;
            CompanyPostFile companyPostFile = CompanyPostFile.createCompanyPostFile(dto, companyPost);
            return fileRepository.save(companyPostFile);

        } else if (REVIEW_FILE.equals(entityType)) {
            Review review = (Review) entity;
            ReviewFile reviewFile = ReviewFile.createReviewFile(dto, review);
            return fileRepository.save(reviewFile);

        } else if (STUDYROOM_FILE.equals(entityType)) {
            StudyRoom studyRoom = (StudyRoom) entity;
            StudyRoomFile studyRoomFile = StudyRoomFile.createStudyRoomFile(dto, studyRoom);
            return fileRepository.save(studyRoomFile);

        } else if (STUDYROOM_POST_FILE.equals(entityType)) {
            StudyRoomPost studyRoomPost = (StudyRoomPost) entity;
            StudyRoomPostFile studyRoomPostFile = StudyRoomPostFile.createStudyRoomPostFile(dto, studyRoomPost);
            return fileRepository.save(studyRoomPostFile);

        } else {
            throw new S3Exception(ErrorCode.S3_EXCEPTION_UNSUPPORTED_TYPE);
        }
    }

    @Transactional
    public void deleteFileByEntityAndS3FileName(String entityType, Long entityId, String s3FileName) {
        int deletedCount;
        switch (entityType) {
            case COMPANY_FILE:
                deletedCount = fileRepository.deleteByCompanyFileAndS3FileName(entityId, s3FileName);
                break;
            case COMPANY_POST_FILE:
                deletedCount = fileRepository.deleteByCompanyPostFileAndS3FileName(entityId, s3FileName);
                break;
            case REVIEW_FILE:
                deletedCount = fileRepository.deleteByReviewFileAndS3FileName(entityId, s3FileName);
                break;
            case STUDYROOM_FILE:
                deletedCount = fileRepository.deleteByStudyRoomFileAndS3FileName(entityId, s3FileName);
                break;
            case STUDYROOM_POST_FILE:
                deletedCount = fileRepository.deleteByStudyRoomPostFileAndS3FileName(entityId, s3FileName);
                break;
            default:
                throw new S3Exception(ErrorCode.S3_EXCEPTION_UNSUPPORTED_TYPE);
        }

        if (deletedCount == 0) {
            throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
        }
    }

    public List<File> findFilesByEntityTypeAndEntityId(String entityType, Long entityId) {
        switch (entityType) {
            case COMPANY_FILE:
                return fileRepository.findCompanyFilesByCompanyId(entityId);
            case COMPANY_POST_FILE:
                return fileRepository.findCompanyPostFilesByCompanyPostId(entityId);
            case REVIEW_FILE:
                return fileRepository.findReviewFilesByReviewId(entityId);
            case STUDYROOM_FILE:
                return fileRepository.findStudyRoomFilesByStudyRoomId(entityId);
            case STUDYROOM_POST_FILE:
                return fileRepository.findStudyRoomPostFilesByStudyRoomPostId(entityId);
            default:
                throw new S3Exception(ErrorCode.S3_EXCEPTION_UNSUPPORTED_TYPE);
        }
    }
}
