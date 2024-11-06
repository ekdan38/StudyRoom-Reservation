package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.web.dto.PendingCompanyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final UserMapper userMapper;

    //Entity => CompanyDto
    public CompanyDto entityToDto(Company entity){
        return new CompanyDto(
                entity.getId(),
                userMapper.entityToUserDto(entity.getUser()),
                entity.getName(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getPhoneNumber());
    }
    //CompanyDto => Entity
    public Company dtoToEntity(CompanyDto dto){
        User user = userMapper.userDtoToEntity(dto.getUserDto());
        return Company.dtoToEntity(dto, user);
    }

}
