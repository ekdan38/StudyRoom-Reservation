package com.jeong.studyroomreservation.domain.entity.user;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.RoleAssigner;
import com.jeong.studyroomreservation.web.dto.signup.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleAssigner roleAssigner;

    //SignupRequestDto => UserDto
    public UserDto requestDtoToUserDto(SignupRequestDto requestDto){

        return new UserDto(
                requestDto.getUsername(),
                requestDto.getPassword(),
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPhoneNumber(),
                roleAssigner.checkUserRole(requestDto));
    }

    // UserEntity => UserDto
    public UserDto entityToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole());
    }
    // UserDto => UserEntity
    public User userDtoToEntity(UserDto userDto){
        return User.dtoToEntity(userDto);
    }

}
