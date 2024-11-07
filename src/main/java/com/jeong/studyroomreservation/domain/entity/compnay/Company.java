package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Company extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_admin_id")
    private User user;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoom> studyRooms = new ArrayList<>(); // 이건 studyroom에서 추가해준다.

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private Company(User user, String name, String description, String location, String phoneNumber) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    //==생성 메서드==//
    public static Company createCompany(CompanyDto dto, User user){
        return new Company(user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber());
    }

    //==수정 메서드==//
    public void updateCompany(CompanyDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.phoneNumber = dto.getPhoneNumber();
    }

    //==연관 관계 메서드==//

    //==비즈니스 로직==//




}
