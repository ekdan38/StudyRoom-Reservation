package com.jeong.studyroomreservation.domain.entity.file;


import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.entity.review.Review;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewFile extends File{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewFile(String originalFileName, String s3FileName, Long fileSize, String fileType) {
        super(originalFileName, s3FileName, fileSize, fileType);
    }

    public static ReviewFile createReviewFile(FileDto dto, Review review){
        ReviewFile reviewFile =
                new ReviewFile(dto.getOriginalFileName(), dto.getS3FileName(), dto.getFileSize(), dto.getFileType());
        reviewFile.setReview(review);
        return reviewFile;
    }

    private void setReview(Review review){
        this.review = review;
        review.getReviewFiles().add(this);
    }
}
