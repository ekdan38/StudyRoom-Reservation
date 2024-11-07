package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.web.dto.CompanyUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    // Entity => Dto
    public CompanyDto entityToDto(Company entity, Long userId){

        return new CompanyDto(
                entity.getId(),
                userId,
                entity.getName(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPhoneNumber());
    }

    // Request => Dto
    public CompanyDto requestToDto(CompanyUpdateDto updateDto){
        return new CompanyDto(
                updateDto.getName(),
                updateDto.getDescription(),
                updateDto.getLocation(),
                updateDto.getPhoneNumber());
    }

}
