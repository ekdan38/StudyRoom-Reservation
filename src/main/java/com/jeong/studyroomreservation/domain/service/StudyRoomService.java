package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoomMapper;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.StudyRoomNotFoundException;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
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

    private final CompanyService companyService;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;

    // 조회, 저장
    // 트랜잭션 전파 돼야됨
    @Transactional
    public StudyRoomDto save(StudyRoomDto dto, UserDto userDto){
        //company를 가져와야한다....
        //지금 각 업체 사장이 요청하는거다... 그러면 어디서 company를 가져오지....
        //userDto에서 id 가져와서 그 id로 company에 조회해보자.
        Company company = companyService.getCompany(userDto.getId());
        StudyRoom savedStudyRoom = studyRoomRepository.save(StudyRoom.createStudyRoom(dto, company));
        return studyRoomMapper.entityToDto(savedStudyRoom);
    }

    // 여러 건 조회
    public Page<StudyRoomDto> getStudyRooms(Pageable pageable, Long companyId) {
//        Page<StudyRoom> page = studyRoomRepository.findAll(pageable);
        Page<StudyRoom> page = studyRoomRepository.findByCompanyId(companyId, pageable);
        return page.map(studyRoomMapper::entityToDto);
    }

    // 단건 조회
    public StudyRoomDto getStudyRoom(Long id){
        StudyRoom studyRoom = studyRoomRepository.findById(id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        return studyRoomMapper.entityToDto(studyRoom);
    }

    // 수정
    @Transactional
    public StudyRoomDto updateStudyRoom(Long id, StudyRoomDto dto){
        StudyRoom foundStudyRoom = studyRoomRepository.findById(id)
                .orElseThrow(() -> new StudyRoomNotFoundException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        foundStudyRoom.updateStudyRoom(dto);
        return studyRoomMapper.entityToDto(foundStudyRoom);
    }

    @Transactional
    public void deleteStudyRoom(Long id) {
        studyRoomRepository.deleteById(id);
    }
}
