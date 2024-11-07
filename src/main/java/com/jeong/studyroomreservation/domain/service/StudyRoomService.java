package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoomMapper;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.StudyRoomNotFoundException;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "[StudyRoomService]")
@Transactional(readOnly = true)
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;
    private final CompanyService companyService;

    // StudyRoom 생성, 저장
    // 조회 쿼리 1번
    // 저장 쿼리 1번
    @Transactional
    public StudyRoomDto createAndSave(StudyRoomDto dto, Long companyId) {
        // Company 조회 쿼리 1번
        Company company = companyService.findById(companyId);
        // StudyRoom 저장 쿼리 1번
        StudyRoom saveadStudyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(dto, company));
        return studyRoomMapper.entityToDto(saveadStudyRoom, null);
    }

    // 페이징으로 StudyRoom들 조회
    public Page<StudyRoomDto> getStudyRooms(Long companyId, Pageable pageable) {
        Page<StudyRoom> page = studyRoomRepository.findAllByCompanyId(companyId, pageable);
        return page.map(p -> studyRoomMapper.entityToDto(p, null));
    }

    // StudyRoom 단건 조회
    public StudyRoomDto getStudyRoom(Long companyId, Long id) {
        StudyRoom foundStudyRoom = studyRoomRepository.findAllByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        return studyRoomMapper.entityToDto(foundStudyRoom, null);
    }

    // StudyRoom 수정
    @Transactional
    public StudyRoomDto updateStudyRoom(StudyRoomDto dto, Long companyId, Long id) {
        StudyRoom foundStudyRoom = studyRoomRepository.findAllByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        foundStudyRoom.updateStudyRoom(dto);
        return studyRoomMapper.entityToDto(foundStudyRoom, null);
    }

    // StudyRoom 삭제
    @Transactional
    public void deleteStudyRoom(Long companyId, Long id) {
        studyRoomRepository.findAllByCompanyIdAndId(companyId, id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        studyRoomRepository.deleteById(id);
    }

}
