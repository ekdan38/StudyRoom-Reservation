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
import com.jeong.studyroomreservation.domain.error.exception.NotFoundException;
import com.jeong.studyroomreservation.domain.error.exception.S3Exception;
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
    private static final String ENTITY_TYPE = "StudyRoomFile";

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

    // StudyRoom 단건 조회(n + 1 해결, LEFT 조인... 기본은 INNER 조인이다.그래서 FETCHJOIN 대상이 없는 경우에는 에러 생긴다..)
    public StudyRoomResponseDto getStudyRoom(Long companyId, Long id) {
        StudyRoom foundStudyRoom = studyRoomRepository.findByCompanyIdAndIdWithStudyRoomFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

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
        StudyRoom foundStudyRoom = studyRoomRepository.findByCompanyIdAndIdWithStudyRoomFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        foundStudyRoom.updateStudyRoom(dto);

        StudyRoomResponseDto studyRoomResponseDto = studyRoomMapper.entityToResponse(foundStudyRoom);

        StudyRoomUpdateResponseDto responseDto =
                studyRoomMapper.responseToUpdateResponse(saveFiles(files, foundStudyRoom, studyRoomResponseDto));

        if (deleteFiles != null) {
            try {
                for (String deleteImage : deleteFiles) {
                    s3ImageUtil.deleteImageFromS3(deleteImage);
                    fileService.deleteFileByEntityAndS3FileName(ENTITY_TYPE, id, deleteImage);
                    responseDto.getDeleteImages().add(deleteImage);
                }
            } catch (Exception e) {
                throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
            }
        }
        return responseDto;
    }


    // StudyRoom 삭제
    @Transactional
    public void deleteStudyRoom(Long companyId, Long id) {
        studyRoomRepository.findByCompanyIdAndIdWithStudyRoomFiles(companyId, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        List<File> studyRoomFile = fileService.findFilesByEntityTypeAndEntityId(ENTITY_TYPE, id);

        try {
            for (File file : studyRoomFile) {
                s3ImageUtil.deleteImageFromS3(file.getS3FileName());
            }
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
        }
        studyRoomRepository.deleteById(id);
    }

    public StudyRoom findByIdWithCompany(Long id) {
        return studyRoomRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
    }
    public StudyRoom findById(Long id){
        return studyRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));

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
                    File studyRoomFile = fileService.createAndSave(ENTITY_TYPE, fileDto, studyRoom);

                    responseDto.getImages().add(studyRoomFile.getS3FileName());
                } catch (Exception e) {
                    s3ImageUtil.deleteImageFromS3(storeFileName);
                    fileService.deleteFileByEntityAndS3FileName(ENTITY_TYPE, studyRoom.getId(), storeFileName);
                    throw new S3Exception(ErrorCode.S3_EXCEPTION_SAVE_DB);
                }
            }
        }
        return responseDto;
    }

}
