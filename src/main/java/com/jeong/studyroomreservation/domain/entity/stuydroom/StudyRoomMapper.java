package com.jeong.studyroomreservation.domain.entity.stuydroom;

import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomUpdateResponseDto;
import com.jeong.studyroomreservation.web.dto.studyroom.StudyRoomRequestDto;
import com.jeong.studyroomreservation.web.dto.studyroom.StudyRoomUpdateRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyRoomMapper {

    // Entity => Dto
    public StudyRoomDto entityToDto(StudyRoom entity, Long companyId){
        return new StudyRoomDto(
                entity.getId(),
                companyId,
                entity.getName(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getRoomState(),
                entity.getTv(),
                entity.getWifi(),
                entity.getWhiteBoard()
        );
    }

    // Request => Dto
    public StudyRoomDto requestToDto(StudyRoomRequestDto requestDto){
        return new StudyRoomDto(
                requestDto.getName(),
                requestDto.getCapacity(),
                requestDto.getPrice(),
                requestDto.getTv(),
                requestDto.getWifi(),
                requestDto.getWhiteBoard()
        );
    }

    // Update => Dto
    public StudyRoomDto updateToDto(StudyRoomUpdateRequestDto updateDto){
        return new StudyRoomDto(
                updateDto.getName(),
                updateDto.getCapacity(),
                updateDto.getPrice(),
                roomState(updateDto.getState()),
                updateDto.getTv(),
                updateDto.getWifi(),
                updateDto.getWhiteBoard()
        );
    }

    // Entity => Response
    public StudyRoomResponseDto entityToResponse(StudyRoom entity){
        return new StudyRoomResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getRoomState(),
                entity.getTv(),
                entity.getWifi(),
                entity.getWhiteBoard()
        );
    }

    // responseDto => updateResponseDto
    public StudyRoomUpdateResponseDto responseToUpdateResponse(StudyRoomResponseDto responseDto){
        StudyRoomUpdateResponseDto updateResponseDto = new StudyRoomUpdateResponseDto(
                responseDto.getId(),
                responseDto.getName(),
                responseDto.getCapacity(),
                responseDto.getPrice(),
                responseDto.getRoomState(),
                responseDto.getTv(),
                responseDto.getWifi(),
                responseDto.getWhiteBoard()
        );
        List<String> images = responseDto.getImages();
        for (String image : images) {
            updateResponseDto.getNewImages().add(image);
        }
        return updateResponseDto;
    }

    private RoomState roomState (String state){
        if(state.equals(RoomState.AVAILABLE.name())){
            return RoomState.AVAILABLE;
        }
        if(state.equals(RoomState.RESERVED.name())){
            return RoomState.RESERVED;
        }
        if(state.equals(RoomState.UNAVAILABLE.name())){
            return RoomState.UNAVAILABLE;
        }
        return RoomState.UNAVAILABLE;
    }
}
