package com.jeong.studyroomreservation.domain.entity.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.web.dto.PendingCompanyRequestDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PendingCompanyMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    //PendingCompanyRequestDto => PendingCompanyDto
    public PendingCompanyDto requestToDto(PendingCompanyRequestDto requestDto, UserDto userDto){
        PendingCompanyDto pendingCompanyDto = modelMapper.map(requestDto, PendingCompanyDto.class);
        pendingCompanyDto.setUserDto(userDto);
        return pendingCompanyDto;
    }

    //Entity => PendingCompanyDto
    public PendingCompanyDto entityToDto(PendingCompany entity){
        return new PendingCompanyDto(entity.getId(), userMapper.entityToUserDto(entity.getUser()),entity.getName(), entity.getDescription(), entity.getLocation(), entity.getPhoneNumber());
    }

    //PendingCompanyDto => Entity
    public PendingCompany dtoToEntity(PendingCompanyDto dto){
        User user = userMapper.userDtoToEntity(dto.getUserDto());
        return PendingCompany.dtoToEntity(dto, user);
    }

}
