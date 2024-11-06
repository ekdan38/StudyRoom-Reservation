package com.jeong.studyroomreservation.domain.entity.stuydroom;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.entity.compnay.CompanyMapper;
import com.jeong.studyroomreservation.web.dto.StudyRoomRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyRoomMapper {

    private final CompanyMapper companyMapper;

    //Entity => Dto
    public StudyRoomDto entityToDto(StudyRoom entity){
        return new StudyRoomDto(
                entity.getId(),
                companyMapper.entityToDto(entity.getCompany()),
                entity.getName(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getRoomState(),
                entity.getTv(),
                entity.getWifi(),
                entity.getWhiteBoard()
        );
    }

    //Dto => Entity
    public StudyRoom dtoToEntity(StudyRoomDto dto){
        return StudyRoom.dtoToEntity(dto, companyMapper.dtoToEntity(dto.getCompanyDto()));
    }
}
