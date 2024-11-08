package com.jeong.studyroomreservation.domain.repository;

import com.jeong.studyroomreservation.domain.entity.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM File f WHERE TYPE(f) = CompanyFile AND f.company.id = :companyId AND f.originalFileName = :originalFileName")
    boolean existsByCompanyFileAndOriginalFileName(@Param("companyId") Long companyId, @Param("originalFileName") String originalFileName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM File f WHERE TYPE(f) = CompanyPostFile AND f.companyPost.id = :companyPostId AND f.originalFileName = :originalFileName")
    boolean existsByCompanyPostFileAndOriginalFileName(@Param("companyPostId") Long companyPostId, @Param("originalFileName") String originalFileName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM File f WHERE TYPE(f) = ReviewFile AND f.review.id = :reviewId AND f.originalFileName = :originalFileName")
    boolean existsByReviewFileAndOriginalFileName(@Param("reviewId") Long reviewId, @Param("originalFileName") String originalFileName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM File f WHERE TYPE(f) = StudyRoomFile AND f.studyRoom.id = :studyRoomId AND f.originalFileName = :originalFileName")
    boolean existsByStudyRoomFileAndOriginalFileName(@Param("studyRoomId") Long studyRoomId, @Param("originalFileName") String originalFileName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM File f WHERE TYPE(f) = StudyRoomPostFile AND f.studyRoomPost.id = :studyRoomPostId AND f.originalFileName = :originalFileName")
    boolean existsByStudyRoomPostFileAndOriginalFileName(@Param("studyRoomPostId") Long studyRoomPostId, @Param("originalFileName") String originalFileName);


    @Modifying
    @Query("DELETE FROM File f WHERE TYPE(f) = CompanyFile AND f.company.id = :companyId AND f.S3FileName = :s3FileName")
    int deleteByCompanyFileAndS3FileName(@Param("companyId") Long companyId, @Param("s3FileName") String S3FileName);

    @Modifying
    @Query("DELETE FROM File f WHERE TYPE(f) = CompanyPostFile AND f.companyPost.id = :companyPostId AND f.S3FileName = :s3FileName")
    int deleteByCompanyPostFileAndS3FileName(@Param("companyPostId") Long companyPostId, @Param("s3FileName") String S3FileName);

    @Modifying
    @Query("DELETE FROM File f WHERE TYPE(f) = ReviewFile AND f.review.id = :reviewId AND f.S3FileName = :s3FileName")
    int deleteByReviewFileAndS3FileName(@Param("reviewId") Long reviewId, @Param("s3FileName") String S3FileName);

    @Modifying
    @Query("DELETE FROM File f WHERE TYPE(f) = StudyRoomFile AND f.studyRoom.id = :studyRoomId AND f.S3FileName = :s3FileName")
    int deleteByStudyRoomFileAndS3FileName(@Param("studyRoomId") Long studyRoomId, @Param("s3FileName") String S3FileName);

    @Modifying
    @Query("DELETE FROM File f WHERE TYPE(f) = StudyRoomPostFile AND f.studyRoomPost.id = :studyRoomPostId AND f.S3FileName = :s3FileName")
    int deleteByStudyRoomPostFileAndS3FileName(@Param("studyRoomPostId") Long studyRoomPostId, @Param("s3FileName") String S3FileName);

    /////
    @Query("SELECT f FROM File f WHERE TYPE(f) = CompanyFile AND f.company.id = :companyId")
    List<File> findCompanyFilesByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT f FROM File f WHERE TYPE(f) = CompanyPostFile AND f.companyPost.id = :companyPostId")
    List<File> findCompanyPostFilesByCompanyPostId(@Param("companyPostId") Long companyPostId);

    @Query("SELECT f FROM File f WHERE TYPE(f) = ReviewFile AND f.review.id = :reviewId")
    List<File> findReviewFilesByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT f FROM File f WHERE TYPE(f) = StudyRoomFile AND f.studyRoom.id = :studyRoomId")
    List<File> findStudyRoomFilesByStudyRoomId(@Param("studyRoomId") Long studyRoomId);

    @Query("SELECT f FROM File f WHERE TYPE(f) = StudyRoomPostFile AND f.studyRoomPost.id = :studyRoomPostId")
    List<File> findStudyRoomPostFilesByStudyRoomPostId(@Param("studyRoomPostId") Long studyRoomPostId);

}
