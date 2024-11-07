package com.jeong.studyroomreservation.domain.entity.stuydroom;

import com.jeong.studyroomreservation.domain.dto.StudyRoomDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.web.dto.studyroom.StudyRoomUpdateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyRoom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; //여기에서 양방향 관계 설정

    private String name;

    private Integer capacity;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private RoomState roomState;

    private Boolean tv;

    private Boolean wifi;

    private Boolean whiteBoard;

    private StudyRoom(Company company, String name, Integer capacity, Integer price, RoomState roomState, Boolean tv, Boolean wifi, Boolean whiteBoard) {
        this.company = company;
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.roomState = roomState;
        this.tv = tv;
        this.wifi = wifi;
        this.whiteBoard = whiteBoard;
    }

    //==생성 메세드==//
    //state는 기본적으로 예약 불가능. requstDto에서 제외하자!
    public static StudyRoom createStudyRoom(StudyRoomDto dto, Company company){
        StudyRoom studyRoom = new StudyRoom(
                null,
                dto.getName(),
                dto.getCapacity(),
                dto.getPrice(),
                RoomState.UNAVAILABLE,
                dto.getTv(),
                dto.getWifi(),
                dto.getWhiteBoard()
        );
        studyRoom.setCompany(company);
        return studyRoom;
    }

    //==연관 관계 메서드==//
    private void setCompany(Company company){
        this.company = company;
        company.getStudyRooms().add(this);
    }

    //==비즈니스 메서드==//
    public void updateStudyRoom(StudyRoomDto dto){
        this.name = dto.getName();
        this.capacity = dto.getCapacity();
        this.price = dto.getPrice();
        this.roomState = dto.getRoomState();
        this.tv = dto.getTv();
        this.wifi = dto.getWifi();
        this.whiteBoard = dto.getWhiteBoard();
    }

}
