package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.company.CompanyResponseDto;
import com.jeong.studyroomreservation.web.dto.company.CompanyUpdateDto;
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

    // update => Dto
    public CompanyDto updateToDto(CompanyUpdateDto updateDto){
        return new CompanyDto(
                updateDto.getName(),
                updateDto.getDescription(),
                updateDto.getLocation(),
                updateDto.getPhoneNumber()
        );
    }

    // Entity => responseDto
    public CompanyResponseDto entityToResponse(Company entity){
        return new CompanyResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPhoneNumber()
        );
    }
}
