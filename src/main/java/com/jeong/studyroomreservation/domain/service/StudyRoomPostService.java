package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.file.StudyRoomPostFile;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPostMapper;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.NotFoundException;
import com.jeong.studyroomreservation.domain.repository.StudyRoomPostRepository;
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
@Slf4j(topic = "[StudyRoomPostService]")
public class StudyRoomPostService {

    private final StudyRoomPostRepository studyRoomPostRepository;
    private final StudyRoomPostMapper studyRoomPostMapper;
    private final StudyRoomService studyRoomService;
    private final S3ImageUtil s3ImageUtil;
    private final FileService fileService;


    // 생성, 저장
    @Transactional
    public StudyRoomPostResponseDto createAndSave(Long studyRoomId, StudyRoomPostDto dto, List<MultipartFile> files) {
        StudyRoom studyRoom = studyRoomService.findById(studyRoomId);

        StudyRoomPost savedStudyRoomPost =
                studyRoomPostRepository.save(StudyRoomPost.createStudyRoomPost(dto, studyRoom));

        StudyRoomPostResponseDto studyRoomPostResponseDto = studyRoomPostMapper.entityToResponse(savedStudyRoomPost);
        return saveFiles(files, savedStudyRoomPost, studyRoomPostResponseDto);
    }

    // 여러 건 조회
    public Page<StudyRoomPostResponseDto> getStudyRoomPosts(Long studyRoomId, Pageable pageable) {
        Page<StudyRoomPost> page = studyRoomPostRepository.findAllByStudyRoomId(studyRoomId, pageable);
        return page.map(p -> {
            StudyRoomPostResponseDto studyRoomPostResponseDto = studyRoomPostMapper.entityToResponse(p);
            List<StudyRoomPostFile> studyRoomPostFiles = p.getStudyRoomPostFiles();
            for (StudyRoomPostFile studyRoomPostFile : studyRoomPostFiles) {
                studyRoomPostResponseDto.getImages().add(studyRoomPostFile.getS3FileName());
            }
            return studyRoomPostResponseDto;
        });
    }

    // 단건 조회
    public StudyRoomPostResponseDto getStudyRoomPost(Long studyRoomId, Long id) {
        StudyRoomPost foundStudyRoomPost = studyRoomPostRepository.findByStudyRoomIdAndIdWithStudyRoomPostFiles(studyRoomId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_POST_NOT_FOUND));

        StudyRoomPostResponseDto studyRoomPostResponseDto = studyRoomPostMapper.entityToResponse(foundStudyRoomPost);
        List<StudyRoomPostFile> studyRoomPostFiles = foundStudyRoomPost.getStudyRoomPostFiles();
        for (StudyRoomPostFile studyRoomPostFile : studyRoomPostFiles) {
            studyRoomPostResponseDto.getImages().add(studyRoomPostFile.getS3FileName());
        }
        return studyRoomPostResponseDto;
    }

    // 수정
    @Transactional
    public StudyRoomPostUpdateResponseDto updateStudyRoomPost(Long studyRoomId, Long id, StudyRoomPostDto dto, List<MultipartFile> files, List<String> deleteFiles) {
        StudyRoomPost foundStudyRoomPost = studyRoomPostRepository.findByStudyRoomIdAndIdWithStudyRoomPostFiles(studyRoomId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_POST_NOT_FOUND));
        foundStudyRoomPost.updateStudyRoomPost(dto);

        StudyRoomPostResponseDto studyRoomPostResponseDto = studyRoomPostMapper.entityToResponse(foundStudyRoomPost);

        StudyRoomPostUpdateResponseDto responseDto =
                studyRoomPostMapper.responseToUpdateResponse(saveFiles(files, foundStudyRoomPost, studyRoomPostResponseDto));


        if (deleteFiles != null) {
            try {
                for (String deleteImage : deleteFiles) {
                    s3ImageUtil.deleteImageFromS3(deleteImage);
                    fileService.deleteFileByEntityAndS3FileName("StudyRoomPostFile", id, deleteImage);
                    responseDto.getDeleteImages().add(deleteImage);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제하다가 오류남.");
            }
        }
        return responseDto;
    }

    // 삭제
    @Transactional
    public void deleteStudyRoomPost(Long studyRoomId, Long id){
        studyRoomPostRepository.findByStudyRoomIdAndIdWithStudyRoomPostFiles(studyRoomId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_POST_NOT_FOUND));
        List<File> studyRoomPostFile = fileService.findFilesByEntityTypeAndEntityId("StudyRoomPostFile", id);

        try{
            for (File file : studyRoomPostFile) {
                s3ImageUtil.deleteImageFromS3(file.getS3FileName());
            }
        } catch (Exception e){
            throw new IllegalArgumentException("지우다가 오류");
        }
        studyRoomPostRepository.deleteById(id);
    }


    private StudyRoomPostResponseDto saveFiles(List<MultipartFile> files, StudyRoomPost studyRoomPost, StudyRoomPostResponseDto responseDto) {
        String storeFileName = null;
        if (files != null) {
            for (MultipartFile file : files) {
                storeFileName = null;
                String originalFilename = file.getOriginalFilename();
                String extention = file.getOriginalFilename()
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

                try {
                    storeFileName = s3ImageUtil.upload(file);

                    FileDto fileDto = new FileDto(originalFilename, storeFileName, file.getSize(), extention);
                    File studyRoomFile = fileService.createAndSave("StudyRoomPostFile", fileDto, studyRoomPost);

                    responseDto.getImages().add(studyRoomFile.getS3FileName());
                } catch (Exception e) {
                    s3ImageUtil.deleteImageFromS3(storeFileName);
                    fileService.deleteFileByEntityAndS3FileName("StudyRoomPostFile", studyRoomPost.getId(), storeFileName);
                    throw new IllegalArgumentException("db에 저장하다가 오류남.");
                }
            }
        }
        return responseDto;
    }
}
