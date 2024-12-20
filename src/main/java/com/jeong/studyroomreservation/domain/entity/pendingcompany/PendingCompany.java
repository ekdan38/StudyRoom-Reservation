package com.jeong.studyroomreservation.domain.entity.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PendingCompany extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //unique 생김
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private PendingCompany(User user, String name, String description, String location, String phoneNumber, LocalTime openingTime, LocalTime closingTime) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    //==생성 메서드==//
    public static PendingCompany createPendingCompany(PendingCompanyDto dto, User user){
        return new PendingCompany(user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber(), dto.getOpeningTime(), dto.getClosingTime());
    }

    //==수정 메서드==//
    public void updatePendingCompany(PendingCompanyDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.phoneNumber = dto.getPhoneNumber();
        this.openingTime = dto.getOpeningTime();
        this.closingTime = dto.getClosingTime();
    }

}
