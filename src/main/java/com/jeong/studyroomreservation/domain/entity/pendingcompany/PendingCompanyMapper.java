package com.jeong.studyroomreservation.domain.entity.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.web.dto.pendingcompany.PendingCompanyRequestDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PendingCompanyMapper {

    private final ModelMapper modelMapper;

    //RequestDto => Dto
    public PendingCompanyDto requestToDto(PendingCompanyRequestDto requestDto, Long userId){
        return new PendingCompanyDto(
                userId,
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getLocation(),
                requestDto.getPhoneNumber());
    }

    //Entity => Dto
    public PendingCompanyDto entityToDto(PendingCompany entity, Long userId){
        return new PendingCompanyDto(
                entity.getId(),
                userId,
                entity.getName(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPhoneNumber());
    }


}
