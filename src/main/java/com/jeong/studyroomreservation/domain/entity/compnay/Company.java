package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.file.CompanyFile;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
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

    @OneToMany(mappedBy = "company",cascade = CascadeType.ALL, orphanRemoval = true )
    private List<CompanyFile> companyFiles = new ArrayList<>();

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;


    private Company(User user, String name, String description, String location, String phoneNumber, LocalTime openingTime, LocalTime closingTime) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    //==생성 메서드==//
    public static Company createCompany(CompanyDto dto, User user){
        return new Company(user, dto.getName(), dto.getDescription(), dto.getLocation(),
                dto.getPhoneNumber(), dto.getOpeningTime(), dto.getClosingTime());
    }

    //==수정 메서드==//
    public void updateCompany(CompanyDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.phoneNumber = dto.getPhoneNumber();
        this.openingTime = dto.getOpeningTime();
        this.closingTime = dto.getClosingTime();
    }

    //==연관 관계 메서드==//

    //==비즈니스 로직==//




}
