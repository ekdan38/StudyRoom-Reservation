package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomUpdateResponseDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.file.StudyRoomFile;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoomMapper;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.StudyRoomNotFoundException;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
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
@Slf4j(topic = "[StudyRoomService]")
@Transactional(readOnly = true)
public class StudyRoomService {

    private final FileService fileService;
    private final S3ImageUtil s3ImageUtil;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;
    private final CompanyService companyService;

    // StudyRoom 생성, 저장
    // 조회 쿼리 1번
    // 저장 쿼리 1번
    @Transactional
    public StudyRoomResponseDto createAndSave(Long companyId, StudyRoomDto dto, List<MultipartFile> files) {
        // Company 조회 쿼리 1번
        Company company = companyService.findById(companyId);
        // StudyRoom 저장 쿼리 1번
        StudyRoom savedStudyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(dto, company));

        StudyRoomResponseDto studyRoomResponseDto = studyRoomMapper.entityToResponse(savedStudyRoom);

        return saveFiles(files, savedStudyRoom, studyRoomResponseDto);
    }

    // 페이징으로 StudyRoom들 조회
    public Page<StudyRoomResponseDto> getStudyRooms(Long companyId, Pageable pageable) {
        Page<StudyRoom> page = studyRoomRepository.findAllByCompanyId(companyId, pageable);
        return page.map(p -> {
            StudyRoomResponseDto studyRoomResponseDto = studyRoomMapper.entityToResponse(p);
            List<StudyRoomFile> studyRoomFiles = p.getStudyRoomFiles();
            for (StudyRoomFile studyRoomFile : studyRoomFiles) {
                studyRoomResponseDto.getImages().add(studyRoomFile.getS3FileName());
            }
            return studyRoomResponseDto;
        });
    }

    // StudyRoom 단건 조회
    public StudyRoomResponseDto getStudyRoom(Long companyId, Long id) {
        StudyRoom foundStudyRoom = studyRoomRepository.findByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

        StudyRoomResponseDto studyRoomResponseDto = studyRoomMapper.entityToResponse(foundStudyRoom);
        List<StudyRoomFile> studyRoomFiles = foundStudyRoom.getStudyRoomFiles();

        for (StudyRoomFile studyRoomFile : studyRoomFiles) {
            studyRoomResponseDto.getImages().add(studyRoomFile.getS3FileName());
        }
        return studyRoomResponseDto;
    }

    // StudyRoom 수정
    @Transactional
    public StudyRoomUpdateResponseDto updateStudyRoom(Long companyId, Long id, StudyRoomDto dto, List<MultipartFile> files, List<String> deleteFiles) {
        StudyRoom foundStudyRoom = studyRoomRepository.findByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        foundStudyRoom.updateStudyRoom(dto);

        StudyRoomResponseDto studyRoomResponseDto = studyRoomMapper.entityToResponse(foundStudyRoom);

        StudyRoomUpdateResponseDto responseDto =
                studyRoomMapper.responseToUpdateResponse(saveFiles(files, foundStudyRoom, studyRoomResponseDto));

        if (deleteFiles != null) {
            try {
                for (String deleteImage : deleteFiles) {
                    s3ImageUtil.deleteImageFromS3(deleteImage);
                    fileService.deleteFileByEntityAndS3FileName("StudyRoomFile", id, deleteImage);
                    responseDto.getDeleteImages().add(deleteImage);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제하다가 오류남.");
            }
        }
        return responseDto;
    }


    // StudyRoom 삭제
    @Transactional
    public void deleteStudyRoom(Long companyId, Long id) {
        studyRoomRepository.findByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        List<File> studyRoomFile = fileService.findFilesByEntityTypeAndEntityId("StudyRoomFile", id);

        try {
            for (File file : studyRoomFile) {
                s3ImageUtil.deleteImageFromS3(file.getS3FileName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("지우다가 오류");
        }
        studyRoomRepository.deleteById(id);
    }

    public StudyRoom findByIdWithCompany(Long id) {
        return studyRoomRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
    }
    public StudyRoom findById(Long id){
        return studyRoomRepository.findById(id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

    }

    private StudyRoomResponseDto saveFiles(List<MultipartFile> files, StudyRoom studyRoom, StudyRoomResponseDto responseDto) {
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
                    File studyRoomFile = fileService.createAndSave("StudyRoomFile", fileDto, studyRoom);

                    responseDto.getImages().add(studyRoomFile.getS3FileName());
                } catch (Exception e) {
                    fileService.deleteFileByEntityAndS3FileName("StudyRoomFile", studyRoom.getId(), storeFileName);
                    throw new IllegalArgumentException("db에 저장하다가 오류남.");
                }
            }
        }
        return responseDto;
    }

}
